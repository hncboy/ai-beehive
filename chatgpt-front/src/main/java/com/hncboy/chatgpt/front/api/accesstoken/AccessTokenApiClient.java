package com.hncboy.chatgpt.front.api.accesstoken;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import lombok.Builder;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.util.concurrent.TimeUnit;

/**
 * @author hncboy
 * @date 2023/3/25 00:32
 * AccessTokenApiClient
 */
@Builder
public class AccessTokenApiClient {

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * 反向代理地址
     */
    private String reverseProxy;

    /**
     * 模型
     */
    private String model;

    /**
     * 创建 OkHttpClient
     */
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    /**
     * 问答接口 stream 形式
     *
     * @param conversationRequest 对话请求参数
     * @param eventSourceListener sse 监听器
     */
    public void streamChatCompletions(ConversationRequest conversationRequest, EventSourceListener eventSourceListener) {
        // 构建请求头
        Headers headers = new Headers.Builder()
                .add(Header.AUTHORIZATION.name(), "Bearer ".concat(accessToken))
                .add(Header.ACCEPT.name(), "text/event-stream")
                .add(Header.CONTENT_TYPE.name(), ContentType.JSON.getValue())
                .build();

        // 构建 Request
        Request request = new Request.Builder()
                .url(reverseProxy)
                .post(RequestBody.create(ObjectMapperUtil.toJson(conversationRequest), MediaType.parse(ContentType.JSON.getValue())))
                .headers(headers)
                .build();
        // 创建事件
        EventSources.createFactory(okHttpClient).newEventSource(request, eventSourceListener);
    }
}

