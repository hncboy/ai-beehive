package com.hncboy.beehive.cell.openai.module.chat.apikey;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import com.hncboy.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatApiModelEnum;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatCellConfigCodeEnum;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/6/10
 * 上下文消息的 Token 数超出模型限制错误处理节点
 */
@Component
public class ChatTokenLimitErrorNode implements ChatErrorNode {

    /**
     * 剩余最小的 Token 数量
     */
    public static final Integer REMAIN_MIN_TOKENS = 10;

    @Override
    public Pair<Boolean, String> doHandle(RoomOpenAiChatMsgDO questionMessage, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        String modelName = questionMessage.getModelName();
        Integer promptTokens = questionMessage.getPromptTokens();
        // 当前模型最大 tokens
        int maxTokens = OpenAiChatApiModelEnum.maxTokens(questionMessage.getModelName());

        boolean isExcelledModelTokenLimit = false;

        String msg = null;
        // 判断 token 数量是否超过限制
        if (OpenAiChatApiModelEnum.maxTokens(modelName) <= promptTokens) {
            isExcelledModelTokenLimit = true;

            // 获取当前 prompt 消耗的 tokens
            int currentPromptTokens = TikTokensUtil.tokens(OpenAiChatApiModelEnum.NAME_MAP.get(modelName).getCalcTokenModelName(), questionMessage.getContent());
            // 判断历史上下文是否超过限制
            int remainingTokens = promptTokens - currentPromptTokens;
            if (OpenAiChatApiModelEnum.maxTokens(modelName) <= remainingTokens) {
                msg = "当前上下文字数已经达到上限，请减少上下文关联的条数";
            } else {
                msg = StrUtil.format("当前上下文 Token 数量：{}，超过上限：{}，请减少字数发送或减少上下文关联的条数", promptTokens, maxTokens);
            }
        }
        // 剩余的 token 太少也直返返回异常信息
        else if (maxTokens - promptTokens <= REMAIN_MIN_TOKENS) {
            isExcelledModelTokenLimit = true;
            msg = "当前上下文字数不足以连续对话，请减少上下文关联的条数";
        }

        // 没有超过限制，继续下一个链路
        if (!isExcelledModelTokenLimit) {
            return new Pair<>(true, null);
        }

        // 超过限制次数更新问题消息
        questionMessage.setStatus(RoomOpenAiChatMsgStatusEnum.EXCEPTION_TOKEN_EXCEED_LIMIT);
        questionMessage.setResponseErrorData(msg);

        return new Pair<>(false, msg);
    }
}
