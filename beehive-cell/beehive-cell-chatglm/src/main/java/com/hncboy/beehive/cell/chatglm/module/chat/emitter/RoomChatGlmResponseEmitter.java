package com.hncboy.beehive.cell.chatglm.module.chat.emitter;

import com.hncboy.beehive.cell.core.hander.strategy.CellConfigStrategy;
import com.hncboy.beehive.cell.chatglm.domain.request.RoomChatGlmSendRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hanpeng
 * @date 2023/8/4
 * ChatGLM 对话房间消息响应转 Emitter
 */
public interface RoomChatGlmResponseEmitter {

    /**
     * 消息请求转 Emitter
     *
     * @param sendRequest        消息处理请求
     * @param emitter            ResponseBodyEmitter
     * @param cellConfigStrategy cell 配置项策略
     */
    void requestToResponseEmitter(RoomChatGlmSendRequest sendRequest, ResponseBodyEmitter emitter, CellConfigStrategy cellConfigStrategy);
}
