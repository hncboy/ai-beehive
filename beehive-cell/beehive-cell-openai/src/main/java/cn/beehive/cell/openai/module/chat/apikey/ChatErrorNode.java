package cn.beehive.cell.openai.module.chat.apikey;

import cn.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import cn.beehive.cell.core.hander.strategy.DataWrapper;
import cn.beehive.cell.openai.enums.OpenAiChatCellConfigCodeEnum;
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
