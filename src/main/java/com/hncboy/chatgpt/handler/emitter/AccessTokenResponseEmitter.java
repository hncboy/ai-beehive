package com.hncboy.chatgpt.handler.emitter;

import com.hncboy.chatgpt.api.accesstoken.AccessTokenApiClient;
import com.hncboy.chatgpt.api.listener.ConsoleStreamListener;
import com.hncboy.chatgpt.api.listener.ParsedEventSourceListener;
import com.hncboy.chatgpt.api.listener.ResponseBodyEmitterStreamListener;
import com.hncboy.chatgpt.api.parser.AccessTokenChatResponseParser;
import com.hncboy.chatgpt.config.ChatConfig;
import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;

/**
 * @author hncboy
 * @date 2023/3/24 13:12
 * AccessToken 响应处理
 */
@Component
public class AccessTokenResponseEmitter implements ResponseEmitter {

    @Resource
    private ChatConfig chatConfig;

    @Override
    public ResponseBodyEmitter requestToResponseEmitter(ChatProcessRequest chatProcessRequest) {
        // 构建 accessTokenApiClient
        AccessTokenApiClient accessTokenApiClient = AccessTokenApiClient.builder()
                .accessToken(chatConfig.getOpenaiAccessToken())
                .reverseProxy(chatConfig.getApiReverseProxy())
                .model(chatConfig.getOpenaiApiModel())
                .build();

        // 构建事件监听器
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(new AccessTokenChatResponseParser())
                .build();

        accessTokenApiClient.streamChatCompletions(chatProcessRequest, parsedEventSourceListener);
        return emitter;
    }
}
