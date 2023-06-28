package com.hncboy.beehive.cell.openai.module.chat.apikey;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import com.hncboy.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import com.hncboy.beehive.base.resource.aip.BaiduAipHandler;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatCellConfigCodeEnum;
import cn.hutool.core.lang.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/6/10
 * 百度内容审核错误节点
 */
@Component
public class ChatBaiduAipErrorNode implements ChatErrorNode {

    @Override
    public Pair<Boolean, String> doHandle(RoomOpenAiChatMsgDO questionMessage, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap) {
        // 判断是否启用
        boolean enabled = roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.ENABLED_BAIDU_AIP).asBoolean();
        if (!enabled) {
            return new Pair<>(true, null);
        }

        // 获取系统消息
        String systemMessage = roomConfigParamAsMap.get(OpenAiChatCellConfigCodeEnum.SYSTEM_MESSAGE).asString();
        // 拼接系统消息
        Pair<Boolean, String> checkTextPassPair = BaiduAipHandler.isCheckTextPass(String.valueOf(questionMessage.getId()), systemMessage.concat(questionMessage.getContent()));
        if (checkTextPassPair.getKey()) {
            return new Pair<>(true, null);
        }

        // 审核失败状态
        questionMessage.setStatus(RoomOpenAiChatMsgStatusEnum.CONTENT_CHECK_FAILURE);
        questionMessage.setResponseErrorData(checkTextPassPair.getValue());

        return new Pair<>(false, "系统消息或发送消息内容不符合规范，请修改后重新发送");
    }
}

