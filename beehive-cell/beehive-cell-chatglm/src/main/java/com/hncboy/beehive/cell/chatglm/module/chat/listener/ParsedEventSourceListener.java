package com.hncboy.beehive.cell.chatglm.module.chat.listener;

import com.hncboy.beehive.cell.chatglm.domain.vo.RoomChatGlmChatMsgVO;
import com.hncboy.beehive.cell.chatglm.module.chat.parser.ChatGlmCompletionResponseParser;
import com.hncboy.beehive.cell.chatglm.module.chat.storage.ChatGlmDatabaseDataStorage;
import com.hncboy.beehive.cell.chatglm.module.chat.storage.RoomChatGlmMessageStorage;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-24
 * 解析 EventSourceListener
 */
@Slf4j
public class ParsedEventSourceListener extends EventSourceListener {

    /**
     * ChatGLM 监听器
     */
    private ChatGlmResponseBodyEmitterStreamListener listener;

    /**
     * 解析器
     */
    private final ChatGlmCompletionResponseParser parser;

    /**
     * 数据存储
     */
    private final ChatGlmDatabaseDataStorage dataStorage;

    /**
     * 聊天消息存储
     */
    private final RoomChatGlmMessageStorage chatMessageStorage;

    private ParsedEventSourceListener(Builder builder) {
        this.listener = builder.listener;
        this.dataStorage = builder.dataStorage;
        this.parser = builder.parser;

        // 初始化聊天消息存储
        this.chatMessageStorage = new RoomChatGlmMessageStorage();
        chatMessageStorage.setCurrentStreamMessageCount(0);
        chatMessageStorage.setReceivedMessage(StrUtil.EMPTY);
        chatMessageStorage.setParser(builder.parser);
        chatMessageStorage.setQuestionMessageDO(builder.questionMessageDO);
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        listener.onInit();
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
        RoomChatGlmChatMsgVO roomChatGlmMsgVO = parser.parseChatMessageVO(chatMessageStorage);

        if (isEnd) {
            listener.onComplete(chatMessageStorage.getReceivedMessage());
        } else {
            listener.onMessage(newMessage, roomChatGlmMsgVO);
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
        RoomChatGlmChatMsgVO roomChatGlmMsgVO = parser.parseChatMessageVO(chatMessageStorage);

        listener.onError(roomChatGlmMsgVO, t, response);
    }

    /**
     * 构建器
     */
    public static class Builder {
        private ChatGlmResponseBodyEmitterStreamListener listener;
        private ChatGlmCompletionResponseParser parser;
        private ChatGlmDatabaseDataStorage dataStorage;

        /**
         * 问题消息实体类
         */
        private Object questionMessageDO;

        public Builder setListener(ChatGlmResponseBodyEmitterStreamListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setParser(ChatGlmCompletionResponseParser parser) {
            this.parser = parser;
            return this;
        }

        public Builder setDataStorage(ChatGlmDatabaseDataStorage dataStorage) {
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
