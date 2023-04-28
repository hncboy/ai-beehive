package com.hncboy.chatgpt.front.handler.emitter;

import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023-3-29
 * ResponseBodyEmitter 链路
 * 责任链模式实现
 */
public interface ResponseEmitterChain {

    /**
     * 处理请求
     *
     * @param request 请求对象
     * @param emitter 响应对象
     */
    void doChain(ChatProcessRequest request, ResponseBodyEmitter emitter);

    /**
     * 设置下一个处理器
     *
     * @param next 下一个处理器
     */
    void setNext(ResponseEmitterChain next);

    /**
     * 获取下一个处理器
     *
     * @return 下一个处理器
     */
    ResponseEmitterChain getNext();

    /**
     * 获取前一个处理器
     *
     * @return 前一个处理器
     */
    ResponseEmitterChain getPrev();

    /**
     * 设置前一个处理器
     *
     * @param prev 前一个处理器
     */
    void setPrev(ResponseEmitterChain prev);
}
