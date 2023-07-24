package com.hncboy.beehive.cell.wxqf.module.chat.storage;

/**
 * @author hncboy
 * @date 2023/7/24
 * 数据存储接口
 */
public interface DataStorage {

    /**
     * 接收到新消息
     *
     * @param roomWxqfChatMessageStorage chatMessageStorage
     */
    void onMessage(RoomWxqfChatMessageStorage roomWxqfChatMessageStorage);

    /**
     * 异常处理
     *
     * @param roomWxqfChatMessageStorage 聊天消息存储
     */
    void onError(RoomWxqfChatMessageStorage roomWxqfChatMessageStorage);
}
