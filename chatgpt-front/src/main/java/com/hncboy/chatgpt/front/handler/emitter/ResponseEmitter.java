package com.hncboy.chatgpt.front.handler.emitter;

import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023-3-24
 * 响应内容
 */
public interface ResponseEmitter {

    /**
     * 消息请求转 Emitter
     *
     * @param chatProcessRequest 消息处理请求
     * @param emitter            ResponseBodyEmitter
     */
    void requestToResponseEmitter(ChatProcessRequest chatProcessRequest, ResponseBodyEmitter emitter);
}
