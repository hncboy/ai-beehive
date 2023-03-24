package com.hncboy.chatgpt.api.listener;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.api.parser.ResponseParser;
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
 * @date 2023/3/24 19:35
 * 解析 EventSourceListener
 */
@Slf4j
public class ParsedEventSourceListener extends EventSourceListener {

    /**
     * 接收到的所有消息
     */
    private final StringBuilder receivedMessage = new StringBuilder();

    /**
     * 当前消息条数
     */
    private int currentMessageCount = 0;

    /**
     * 监听器列表
     */
    private final List<AbstractStreamListener> listeners;

    /**
     * 解析器
     */
    private final ResponseParser<?> parser;

    private ParsedEventSourceListener(Builder builder) {
        this.listeners = builder.listeners;
        this.parser = builder.parser;
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String originalData) {
        // 判断有没有结束
        boolean isEnd = Objects.equals(originalData, "[DONE]");
        String newMessage = null;
        if (!isEnd) {
            // 解析消息
            newMessage = parser.parseContent(originalData);
            // 为空直接跳过
            if (StrUtil.isEmpty(newMessage)) {
                return;
            }
            // 添加新的消息
            receivedMessage.append(newMessage);
        }

        // 遍历监听器
        currentMessageCount++;
        for (AbstractStreamListener listener : listeners) {
            if (isEnd) {
                listener.onComplete(receivedMessage.toString());
                listener.onComplete.accept(receivedMessage.toString());
            } else {
                listener.onMessage(newMessage, receivedMessage.toString(), currentMessageCount);
            }
        }
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        try {
            String responseStr = null;
            if (Objects.nonNull(response) && Objects.nonNull(response.body())) {
                responseStr = response.body().string();
            }
            log.warn("消息发送异常，当前已接收消息：{}，响应内容：{}", receivedMessage.toString(), responseStr, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (AbstractStreamListener listener : listeners) {
            listener.onError(receivedMessage.toString(), t, response);
        }
    }

    /**
     * 构建器
     */
    public static class Builder {
        private final List<AbstractStreamListener> listeners = new ArrayList<>();
        private ResponseParser<?> parser;

        public Builder addListener(AbstractStreamListener listener) {
            listeners.add(listener);
            return this;
        }

        public Builder setParser(ResponseParser<?> parser) {
            this.parser = parser;
            return this;
        }

        public ParsedEventSourceListener build() {
            if (parser == null) {
                throw new IllegalStateException("Parser must be set");
            }
            return new ParsedEventSourceListener(this);
        }
    }
}
