package com.hncboy.chatgpt.front.handler.emitter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.util.concurrent.RateLimiter;
import com.hncboy.chatgpt.base.service.IpLimitService;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.base.util.WebUtil;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.domain.vo.ChatReplyMessageVO;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Optional;

/**
 * @author lizhongyuan
 */
@AllArgsConstructor
public class IpLimitEmitterChain extends AbstractResponseEmitterChain {

    @Override
    public void doChain(ChatProcessRequest request, ResponseBodyEmitter emitter) {
        try {
            String ip = WebUtil.getIp();
            IpLimitService ipLimitService = SpringUtil.getBean(IpLimitService.class);
            RateLimiter limiter = ipLimitService.getIpLimiter(ip);
            if (limiter != null && limiter.tryAcquire()) {
                if (getNext() != null) {
                    getNext().doChain(request, emitter);
                }
            } else {
                ChatReplyMessageVO chatReplyMessageVO = ChatReplyMessageVO.onEmitterChainException(request);
                chatReplyMessageVO.setText(StrUtil.format("当前 ip:{} 访问过多，请等待", ip));
                emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
                emitter.complete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
