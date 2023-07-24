package com.hncboy.beehive.cell.wxqf.module.chat.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonResponse;
import com.hncboy.beehive.cell.wxqf.module.chat.parser.ChatCommonResponseParser;
import com.hncboy.beehive.cell.wxqf.module.chat.storage.ChatCommonDatabaseDataStorage;
import com.hncboy.beehive.cell.wxqf.module.chat.storage.RoomWxqfChatMessageStorage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/7/24
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
    private final ChatCommonResponseParser parser;

    /**
     * 数据存储
     */
    private final ChatCommonDatabaseDataStorage dataStorage;

    /**
     * 聊天消息存储
     */
    private final RoomWxqfChatMessageStorage chatMessageStorage;

    private ParsedEventSourceListener(Builder builder) {
        this.listeners = builder.listeners;
        this.dataStorage = SpringUtil.getBean(ChatCommonDatabaseDataStorage.class);
        this.parser = SpringUtil.getBean(ChatCommonResponseParser.class);

        // 初始化聊天消息存储
        this.chatMessageStorage = new RoomWxqfChatMessageStorage();
        chatMessageStorage.setParser(parser);
        chatMessageStorage.setApiCommonResponses(new LinkedList<>());
        chatMessageStorage.setReceivedMessage(StrUtil.EMPTY);
        chatMessageStorage.setQuestionMessageDO((RoomWxqfChatMsgDO) builder.questionMessageDO);
        chatMessageStorage.setAnswerMessageDO(new RoomWxqfChatMsgDO());
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String originalData) {
        WxqfChatApiCommonResponse chatApiCommonResponse = parser.parseSuccess(originalData);
        // 记录本次响应数据
        chatMessageStorage.getApiCommonResponses().add(chatApiCommonResponse);

        // 解析消息
        String newMessage = parser.parseNewMessage(originalData);

        // 当前收到的所有消息
        chatMessageStorage.setReceivedMessage(parser.parseReceivedMessage(chatMessageStorage.getReceivedMessage(), newMessage));

        // 调用存新消息方法
        dataStorage.onMessage(chatMessageStorage);

        // 解析消息展示参数
        RoomWxqfChatMsgVO roomWxqfChatMsgVO = parser.parseChatMessageVO(chatMessageStorage);

        // 遍历监听器
        for (AbstractStreamListener listener : listeners) {
            listener.onMessage(chatApiCommonResponse, newMessage, roomWxqfChatMsgVO);
        }
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        String responseStr = null;
        try {
            if (Objects.nonNull(response) && Objects.nonNull(response.body())) {
                responseStr = response.body().string();
            }
            log.warn("文心一帆消息发送异常，当前已接收消息：{}，响应内容：{}，异常堆栈：", chatMessageStorage.getReceivedMessage(), responseStr, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 调用存储异常方法
        chatMessageStorage.setErrorResponseData(responseStr);
        dataStorage.onError(chatMessageStorage);

        // 解析消息展示参数
        RoomWxqfChatMsgVO roomWxqfChatMsgVO = parser.parseChatMessageVO(chatMessageStorage);

        // 遍历监听器发送异常消息
        for (AbstractStreamListener listener : listeners) {
            listener.onError(roomWxqfChatMsgVO, t, response);
        }
    }

    /**
     * 构建器
     */
    public static class Builder {
        private final List<AbstractStreamListener> listeners = new ArrayList<>();

        /**
         * 问题消息实体类
         */
        private Object questionMessageDO;

        public Builder addListener(AbstractStreamListener listener) {
            listeners.add(listener);
            return this;
        }

        public Builder setQuestionMessageDO(Object questionMessageDO) {
            this.questionMessageDO = questionMessageDO;
            return this;
        }

        public ParsedEventSourceListener build() {
            if (questionMessageDO == null) {
                throw new IllegalStateException("ChatMessageDO must be set");
            }
            return new ParsedEventSourceListener(this);
        }
    }
}
