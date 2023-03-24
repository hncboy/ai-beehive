package com.hncboy.chatgpt.handler.emitter;

import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.exception.ServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023/3/24 13:12
 * AccessToken 响应处理
 */
@Component
public class AccessTokenResponseEmitter implements ResponseEmitter {

    @Override
    public ResponseBodyEmitter requestToResponseEmitter(ChatProcessRequest chatProcessRequest) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        try {
            throw new ServiceException("AccessToken 暂未开发");
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }
}
