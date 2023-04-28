package com.hncboy.chatgpt.front.api.storage;

import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.front.api.parser.ResponseParser;
import lombok.Builder;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023-3-25
 * 聊天消息数据存储业业务参数
 */
@Data
@Builder
public class ChatMessageStorage {

    /**
     * 问题聊天记录
     */
    private ChatMessageDO questionChatMessageDO;

    /**
     * 回答聊天记录
     */
    private ChatMessageDO answerChatMessageDO;

    /**
     * 原始请求数据
     */
    private String originalRequestData;

    /**
     * 原始响应数据
     */
    private String originalResponseData;

    /**
     * 异常响应数据
     */
    private String errorResponseData;

    /**
     * 响应解析器
     */
    private ResponseParser<?> parser;

    /**
     * 当前消息流条数
     */
    private int currentStreamMessageCount;
}
