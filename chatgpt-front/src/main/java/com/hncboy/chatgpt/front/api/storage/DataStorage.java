package com.hncboy.chatgpt.front.api.storage;

/**
 * @author hncboy
 * @date 2023-3-25
 * 数据存储接口
 */
public interface DataStorage {

    /**
     * 接收到新消息
     *
     * @param chatMessageStorage chatMessageStorage
     */
    void onMessage(ChatMessageStorage chatMessageStorage);

    /**
     * 结束响应
     *
     * @param chatMessageStorage 聊天消息存储
     */
    void onComplete(ChatMessageStorage chatMessageStorage);

    /**
     * 异常处理
     *
     * @param chatMessageStorage 聊天消息存储
     */
    void onError(ChatMessageStorage chatMessageStorage);
}
