package com.hncboy.chatgpt.front.service.impl;

import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.handler.emitter.ChatMessageEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.IpRateLimiterEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.ResponseEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.SensitiveWordEmitterChain;
import com.hncboy.chatgpt.front.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/3/22 19:41
 * 聊天相关业务实现类
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Override
    public ResponseBodyEmitter chatProcess(ChatProcessRequest chatProcessRequest) {
        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));
        emitter.onTimeout(() -> log.error("请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));

        // 构建 emitter 处理链路
        ResponseEmitterChain ipRateLimiterEmitterChain = new IpRateLimiterEmitterChain();
        ResponseEmitterChain sensitiveWordEmitterChain = new SensitiveWordEmitterChain();
        sensitiveWordEmitterChain.setNext(new ChatMessageEmitterChain());
        ipRateLimiterEmitterChain.setNext(sensitiveWordEmitterChain);
        ipRateLimiterEmitterChain.doChain(chatProcessRequest, emitter);
        return emitter;
    }
}
