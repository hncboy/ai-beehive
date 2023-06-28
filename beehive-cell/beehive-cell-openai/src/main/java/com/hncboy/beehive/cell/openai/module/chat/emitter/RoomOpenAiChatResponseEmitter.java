package com.hncboy.beehive.cell.openai.module.chat.emitter;

import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023-3-24
 * OpenAi 对话房间消息响应转 Emitter
 */
public interface RoomOpenAiChatResponseEmitter {

    /**
     * 消息请求转 Emitter
     *
     * @param sendRequest        消息处理请求
     * @param emitter            ResponseBodyEmitter
     * @param cellConfigStrategy cell 配置项策略
     */
    void requestToResponseEmitter(RoomOpenAiChatSendRequest sendRequest, ResponseBodyEmitter emitter, CellConfigStrategy cellConfigStrategy);
}
