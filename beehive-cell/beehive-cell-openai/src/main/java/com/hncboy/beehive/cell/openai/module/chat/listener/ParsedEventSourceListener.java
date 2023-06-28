package com.hncboy.beehive.cell.openai.module.chat.listener;

import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import com.hncboy.beehive.cell.openai.module.chat.parser.ResponseParser;
import com.hncboy.beehive.cell.openai.module.chat.storage.DataStorage;
import com.hncboy.beehive.cell.openai.module.chat.storage.RoomOpenAiChatMessageStorage;
import cn.hutool.core.util.StrUtil;
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
     * 聊天消息存储
     */
    private final RoomOpenAiChatMessageStorage chatMessageStorage;

    private ParsedEventSourceListener(Builder builder) {
        this.listeners = builder.listeners;
        this.dataStorage = builder.dataStorage;
        this.parser = builder.parser;

        // 初始化聊天消息存储
        this.chatMessageStorage = new RoomOpenAiChatMessageStorage();
        chatMessageStorage.setCurrentStreamMessageCount(0);
        chatMessageStorage.setReceivedMessage(StrUtil.EMPTY);
        chatMessageStorage.setParser(builder.parser);
        chatMessageStorage.setQuestionMessageDO(builder.questionMessageDO);
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
        if (isEnd) {
            // 调用存储结束方法
            dataStorage.onComplete(chatMessageStorage);
        } else {
            // 解析消息
            newMessage = parser.parseNewMessage(originalData);
            // 为空直接跳过
            if (StrUtil.isEmpty(newMessage)) {
                return;
            }

            // 消息数量 +1
            chatMessageStorage.setCurrentStreamMessageCount(chatMessageStorage.getCurrentStreamMessageCount() + 1);

            // 当前收到的所有消息
            chatMessageStorage.setReceivedMessage(parser.parseReceivedMessage(chatMessageStorage.getReceivedMessage(), newMessage));

            // 记录上次响应数据
            chatMessageStorage.setOriginalResponseData(originalData);

            // 调用存新消息方法
            dataStorage.onMessage(chatMessageStorage);
        }

        // 解析消息展示参数
        RoomOpenAiChatMsgVO roomOpenAiChatMsgVO = parser.parseChatMessageVO(chatMessageStorage);

        // 遍历监听器
        for (AbstractStreamListener listener : listeners) {
            if (isEnd) {
                listener.onComplete(chatMessageStorage.getReceivedMessage());
            } else {
                listener.onMessage(newMessage, roomOpenAiChatMsgVO);
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
            log.warn("消息发送异常，当前已接收消息：{}，响应内容：{}，异常堆栈：", chatMessageStorage.getReceivedMessage(), responseStr, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 调用存储异常方法
        chatMessageStorage.setErrorResponseData(responseStr);
        dataStorage.onError(chatMessageStorage);

        // 解析消息展示参数
        RoomOpenAiChatMsgVO roomOpenAiChatMsgVO = parser.parseChatMessageVO(chatMessageStorage);

        // 遍历监听器发送异常消息
        for (AbstractStreamListener listener : listeners) {
            listener.onError(roomOpenAiChatMsgVO, t, response);
        }
    }

    /**
     * 构建器
     */
    public static class Builder {
        private final List<AbstractStreamListener> listeners = new ArrayList<>();
        private ResponseParser<?> parser;
        private DataStorage dataStorage;

        /**
         * 问题消息实体类
         */
        private Object questionMessageDO;

        public Builder addListener(AbstractStreamListener listener) {
            listeners.add(listener);
            return this;
        }

        public Builder setParser(ResponseParser<?> parser) {
            this.parser = parser;
            return this;
        }

        public Builder setDataStorage(DataStorage dataStorage) {
            this.dataStorage = dataStorage;
            return this;
        }

        public Builder setQuestionMessageDO(Object questionMessageDO) {
            this.questionMessageDO = questionMessageDO;
            return this;
        }

        public ParsedEventSourceListener build() {
            if (parser == null) {
                throw new IllegalStateException("Parser must be set");
            }
            if (dataStorage == null) {
                throw new IllegalStateException("DataStorage must be set");
            }
            if (questionMessageDO == null) {
                throw new IllegalStateException("ChatMessageDO must be set");
            }
            return new ParsedEventSourceListener(this);
        }
    }
}
