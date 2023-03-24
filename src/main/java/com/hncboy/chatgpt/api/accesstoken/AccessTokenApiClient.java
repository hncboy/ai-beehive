package com.hncboy.chatgpt.api.accesstoken;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.exception.ServiceException;
import com.hncboy.chatgpt.util.ObjectMapperUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Builder;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
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
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * 问答接口 stream 形式
     *
     * @param chatProcessRequest  消息处理请求参数
     * @param eventSourceListener sse 监听器
     */
    public void streamChatCompletions(ChatProcessRequest chatProcessRequest, EventSourceListener eventSourceListener) {
        boolean isFirstMessage = isFirstMessage(chatProcessRequest);

        // 构建 content
        ConversationRequest.Content content = ConversationRequest.Content.builder()
                .parts(Collections.singletonList(chatProcessRequest.getPrompt()))
                .build();

        // 构建 Message
        ConversationRequest.Message message = ConversationRequest.Message.builder()
                .id(UUID.randomUUID().toString())
                .role(Message.Role.USER.getName())
                .content(content)
                .build();

        // 构建 ConversationRequest
        ConversationRequest conversationRequest = ConversationRequest.builder()
                .messages(Collections.singletonList(message))
                .action(ConversationRequest.MessageActionTypeEnum.NEXT)
                .model(StrUtil.isEmpty(model) ? ConversationModelEnum.DEFAULT_GPT_3_5 : ConversationModelEnum.NAME_MAP.get(model))
                // 第一条消息随机取一个父级消息 id
                .parentMessageId(isFirstMessage ? UUID.randomUUID().toString() : chatProcessRequest.getOptions().getParentMessageId())
                .conversationId(isFirstMessage ? null : chatProcessRequest.getOptions().getConversationId())
                .build();

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

    /**
     * 判断是不是第一条消息
     *
     * @param chatProcessRequest 消息请求处理
     * @return true/false
     */
    private boolean isFirstMessage(ChatProcessRequest chatProcessRequest) {
        ChatProcessRequest.Options options = chatProcessRequest.getOptions();
        if (Objects.isNull(options)) {
            return true;
        }
        // 都为空说明是第一条消息
        if (ObjectUtil.isAllEmpty(options.getParentMessageId(), options.getConversationId())) {
            return true;
        }
        if (ObjectUtil.isAllNotEmpty(options.getParentMessageId(), options.getConversationId())) {
            return false;
        }
        throw new ServiceException("parentMessageId 和conversationId 必须一起有值");
    }
}

