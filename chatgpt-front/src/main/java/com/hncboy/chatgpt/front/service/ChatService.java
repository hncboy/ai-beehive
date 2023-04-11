package com.hncboy.chatgpt.front.service;

import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/3/22 19:41
 * 聊天相关业务接口
 */
public interface ChatService {

    /**
     * 消息处理
     *
     * @param chatProcessRequest 消息处理请求参数
     * @return emitter
     */
    ResponseBodyEmitter chatProcess(ChatProcessRequest chatProcessRequest);
}
