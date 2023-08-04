package com.hncboy.beehive.cell.chatglm.module.chat.api;

import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.base.enums.RoomChatGlmMsgStatusEnum;
import com.hncboy.beehive.cell.chatglm.enums.ChatGlmApiModelEnum;
import com.hncboy.beehive.cell.chatglm.enums.ChatGlmCellConfigCodeEnum;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hanpeng
 * @date 2023/8/4
 * 上下文消息的 Token 数超出模型限制错误处理节点
 */
@Component
public class ChatGlmTokenLimitErrorNode implements ChatGlmErrorNode {

    /**
     * 剩余最小的 Token 数量
     */
    public static final Integer REMAIN_MIN_TOKENS = 10;

    @Override
    public Pair<Boolean, String> doHandle(RoomChatGlmMsgDO questionMessage, Map<ChatGlmCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        String modelName = questionMessage.getModelName();
        Integer promptTokens = questionMessage.getPromptTokens();
        // 当前模型最大 tokens
        int maxTokens = ChatGlmApiModelEnum.maxTokens(questionMessage.getModelName());

        boolean isExcelledModelTokenLimit = false;

        String msg = null;
        // 判断 token 数量是否超过限制
        if (ChatGlmApiModelEnum.maxTokens(modelName) <= promptTokens) {
            isExcelledModelTokenLimit = true;

            // 获取当前 prompt 消耗的 tokens
            int currentPromptTokens = TikTokensUtil.tokens(ChatCompletion.Model.GPT_3_5_TURBO.getName(), questionMessage.getContent());
            // 判断历史上下文是否超过限制
            int remainingTokens = promptTokens - currentPromptTokens;
            if (ChatGlmApiModelEnum.maxTokens(modelName) <= remainingTokens) {
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
        questionMessage.setStatus(RoomChatGlmMsgStatusEnum.EXCEPTION_TOKEN_EXCEED_LIMIT);
        questionMessage.setResponseErrorData(msg);

        return new Pair<>(false, msg);
    }
}
