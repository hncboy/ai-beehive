package com.hncboy.chatgpt.front.handler.emitter;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.handler.IpRateLimiterHandler;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.base.util.WebUtil;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.domain.vo.ChatReplyMessageVO;
import lombok.AllArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;

/**
 * @author lizhongyuan
 * Ip 限流处理
 */
@AllArgsConstructor
public class IpRateLimiterEmitterChain extends AbstractResponseEmitterChain {

    @Override
    public void doChain(ChatProcessRequest request, ResponseBodyEmitter emitter) {
        try {
            String ip = WebUtil.getIp();
            // 根据ip判断是够可放行
            if (IpRateLimiterHandler.allowRequest(ip)) {
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
