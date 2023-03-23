package com.hncboy.chatgpt.service;

import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.domain.vo.ChatConfigVO;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/3/22 19:41
 * 聊天相关业务接口
 */
public interface ChatService {

    /**
     * 获取聊天配置
     *
     * @return 聊天配置
     */
    ChatConfigVO getChatConfig();

    /**
     * 消息处理
     *
     * @param chatProcessRequest 消息处理请求参数
     * @return emitter
     */
    ResponseBodyEmitter chatProcess(ChatProcessRequest chatProcessRequest);
}
