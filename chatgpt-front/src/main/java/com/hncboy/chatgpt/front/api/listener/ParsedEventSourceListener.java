package com.hncboy.chatgpt.front.api.listener;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.front.api.parser.ResponseParser;
import com.hncboy.chatgpt.front.api.storage.ChatMessageStorage;
import com.hncboy.chatgpt.front.api.storage.DataStorage;
import com.hncboy.chatgpt.front.domain.vo.ChatReplyMessageVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-24
 * 解析 EventSourceListener
 */
@Slf4j
public class ParsedEventSourceListener extends EventSourceListener {

    /**
     * 已经接收到的完整消息
     */
    private String receivedMessage = StrUtil.EMPTY;

    /**
     * 当前消息流条数
     */
    private int currentStreamMessageCount = 0;

    /**
     * 监听器列表
     */
    private final List<AbstractStreamListener> listeners;

    /**
     * 解析器
     */
    private final ResponseParser<?> parser;

    /**
     * 数据存储
     */
    private final DataStorage dataStorage;

    /**
     * 原始发送的请求数据
     */
    private final String originalRequestData;

    /**
     * 问题聊天记录
     */
    private final ChatMessageDO questionChatMessageDO;

    /**
     * 回答聊天记录
     */
    private final ChatMessageDO answerChatMessageDO;

    /**
     * 上次原始响应数据
     */
    private String lastOriginalResponseData;

    private ParsedEventSourceListener(Builder builder) {
        this.listeners = builder.listeners;
        this.parser = builder.parser;
        this.dataStorage = builder.dataStorage;
        this.originalRequestData = builder.originalRequestData;
        this.questionChatMessageDO = builder.chatMessageDO;
        this.answerChatMessageDO = new ChatMessageDO();
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        for (AbstractStreamListener listener : listeners) {
            listener.onInit();
        }
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String originalData) {
        // 判断有没有结束
        boolean isEnd = Objects.equals(originalData, "[DONE]");
        String newMessage = null;
        ChatReplyMessageVO chatReplyMessageVO = null;

        if (isEnd) {
            // 调用存储结束方法
            dataStorage.onComplete(ChatMessageStorage.builder()
                    .questionChatMessageDO(questionChatMessageDO)
                    .answerChatMessageDO(answerChatMessageDO)
                    .originalRequestData(originalRequestData)
                    .originalResponseData(lastOriginalResponseData)
                    .parser(parser)
                    .build());
        } else {
            // 解析消息
            newMessage = parser.parseNewMessage(originalData);
            // 为空直接跳过
            if (StrUtil.isEmpty(newMessage)) {
                return;
            }

            // 消息数量 +1
            currentStreamMessageCount++;

            // 当前收到的所有消息
            receivedMessage = parser.parseReceivedMessage(receivedMessage, newMessage);

            // 记录上次响应数据
            lastOriginalResponseData = originalData;

            // 设置已经接收到消息内容
            answerChatMessageDO.setContent(receivedMessage);

            // 调用存新消息方法
            dataStorage.onMessage(ChatMessageStorage.builder()
                    .questionChatMessageDO(questionChatMessageDO)
                    .answerChatMessageDO(answerChatMessageDO)
                    .originalRequestData(originalRequestData)
                    .originalResponseData(lastOriginalResponseData)
                    .currentStreamMessageCount(currentStreamMessageCount)
                    .parser(parser)
                    .build());

            // 构建 ChatReplyMessageVO
            chatReplyMessageVO = new ChatReplyMessageVO();
            chatReplyMessageVO.setId(answerChatMessageDO.getMessageId());
            chatReplyMessageVO.setRole(null);
            chatReplyMessageVO.setParentMessageId(answerChatMessageDO.getParentMessageId());
            chatReplyMessageVO.setConversationId(answerChatMessageDO.getConversationId());
            chatReplyMessageVO.setText(receivedMessage);
        }

        // 遍历监听器
        for (AbstractStreamListener listener : listeners) {
            if (isEnd) {
                listener.onComplete(receivedMessage);
            } else {
                listener.onMessage(newMessage, receivedMessage, chatReplyMessageVO, currentStreamMessageCount);
            }
        }
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        String responseStr = null;
        try {
            if (Objects.nonNull(response) && Objects.nonNull(response.body())) {
                responseStr = response.body().string();
            }
            log.warn("消息发送异常，当前已接收消息：{}，响应内容：{}，异常堆栈：", receivedMessage, responseStr, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 调用存储异常方法
        dataStorage.onError(ChatMessageStorage.builder()
                .questionChatMessageDO(questionChatMessageDO)
                .answerChatMessageDO(answerChatMessageDO)
                .originalRequestData(originalRequestData)
                .originalResponseData(lastOriginalResponseData)
                .currentStreamMessageCount(currentStreamMessageCount)
                .errorResponseData(responseStr)
                .parser(parser)
                .build());

        // 遍历监听器发送异常消息
        for (AbstractStreamListener listener : listeners) {
            listener.onError(receivedMessage, t, response);
        }
    }

    /**
     * 构建器
     */
    public static class Builder {
        private final List<AbstractStreamListener> listeners = new ArrayList<>();
        private ResponseParser<?> parser;
        private String originalRequestData;
        private DataStorage dataStorage;
        private ChatMessageDO chatMessageDO;

        public Builder addListener(AbstractStreamListener listener) {
            listeners.add(listener);
            return this;
        }

        public Builder setParser(ResponseParser<?> parser) {
            this.parser = parser;
            return this;
        }

        public Builder setOriginalRequestData(String originalRequestData) {
            this.originalRequestData = originalRequestData;
            return this;
        }

        public Builder setDataStorage(DataStorage dataStorage) {
            this.dataStorage = dataStorage;
            return this;
        }

        public Builder setChatMessageDO(ChatMessageDO chatMessageDO) {
            this.chatMessageDO = chatMessageDO;
            return this;
        }

        public ParsedEventSourceListener build() {
            if (parser == null) {
                throw new IllegalStateException("Parser must be set");
            }
            if (dataStorage == null) {
                throw new IllegalStateException("DataStorage must be set");
            }
            if (originalRequestData == null) {
                throw new IllegalStateException("originalRequestData must be set");
            }
            if (chatMessageDO == null) {
                throw new IllegalStateException("ChatMessageDO must be set");
            }
            return new ParsedEventSourceListener(this);
        }
    }
}
