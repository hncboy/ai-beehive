package com.hncboy.beehive.cell.chatglm.module.chat.api;

import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.cell.chatglm.enums.ChatGlmCellConfigCodeEnum;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import cn.hutool.core.lang.Pair;

import java.util.Map;

/**
 * @author hanpeng
 * @date 2023/8/4
 * ChatGLM 对话错误链路
 */
public interface ChatGlmErrorNode {

    /**
     * 处理问题消息
     *
     * @param questionMessage      问题消息
     * @param roomConfigParamAsMap 房间配置参数
     * @return 结果 key：是否通过 value：错误内容
     */
    Pair<Boolean, String> doHandle(RoomChatGlmMsgDO questionMessage, Map<ChatGlmCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap);
}
