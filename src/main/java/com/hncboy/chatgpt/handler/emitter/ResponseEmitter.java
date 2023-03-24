package com.hncboy.chatgpt.handler.emitter;

import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/3/24 13:07
 * 响应内容
 */
public interface ResponseEmitter {

    /**
     * 消息请求转 Emitter
     *
     * @param chatProcessRequest 消息处理请求
     * @return ResponseBodyEmitter
     */
    ResponseBodyEmitter requestToResponseEmitter(ChatProcessRequest chatProcessRequest);
}
