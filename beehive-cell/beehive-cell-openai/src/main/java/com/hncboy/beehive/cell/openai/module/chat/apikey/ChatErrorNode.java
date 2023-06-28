package com.hncboy.beehive.cell.openai.module.chat.apikey;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import com.hncboy.beehive.cell.core.hander.strategy.DataWrapper;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatCellConfigCodeEnum;
import cn.hutool.core.lang.Pair;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/6/10
 * Openai 对话错误链路
 */
public interface ChatErrorNode {

    /**
     * 处理问题消息
     *
     * @param questionMessage      问题消息
     * @param roomConfigParamAsMap 房间配置参数
     * @return 结果 key：是否通过 value：错误内容
     */
    Pair<Boolean, String> doHandle(RoomOpenAiChatMsgDO questionMessage, Map<OpenAiChatCellConfigCodeEnum, DataWrapper> roomConfigParamAsMap);
}
