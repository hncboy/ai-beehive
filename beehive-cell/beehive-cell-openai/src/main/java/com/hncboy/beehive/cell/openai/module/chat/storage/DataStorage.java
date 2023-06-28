package com.hncboy.beehive.cell.openai.module.chat.storage;

/**
 * @author hncboy
 * @date 2023-3-25
 * 数据存储接口
 */
public interface DataStorage {

    /**
     * 接收到新消息
     *
     * @param roomOpenAiChatMessageStorage chatMessageStorage
     */
    void onMessage(RoomOpenAiChatMessageStorage roomOpenAiChatMessageStorage);

    /**
     * 结束响应
     *
     * @param roomOpenAiChatMessageStorage 聊天消息存储
     */
    void onComplete(RoomOpenAiChatMessageStorage roomOpenAiChatMessageStorage);

    /**
     * 异常处理
     *
     * @param roomOpenAiChatMessageStorage 聊天消息存储
     */
    void onError(RoomOpenAiChatMessageStorage roomOpenAiChatMessageStorage);
}
