package com.hncboy.beehive.cell.wxqf.module.chat.storage;

import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonResponse;

/**
 * @author hncboy
 * @date 2023/7/24
 * 数据库数据存储抽象类
 */
public abstract class AbstractDatabaseDataStorage implements DataStorage {

    @Override
    public void onMessage(RoomWxqfChatMessageStorage chatMessageStorage) {
        // 获取最后一条
        WxqfChatApiCommonResponse lastChatApiCommonResponse = chatMessageStorage.getApiCommonResponses().getLast();
        // 为 0 表示第一条消息
        if (lastChatApiCommonResponse.getSentenceId() == 0) {
            // 第一条消息
            onFirstMessage(chatMessageStorage);
        }

        // 最后一条消息，第一条消息可能也是最后一条
        if (lastChatApiCommonResponse.getIsEnd()) {
            onLastMessage(chatMessageStorage);
        }
    }

    /**
     * 收到第一条消息
     *
     * @param chatMessageStorage chatMessageStorage 聊天记录存储
     */
    abstract void onFirstMessage(RoomWxqfChatMessageStorage chatMessageStorage);

    /**
     * 收到最后第一条消息
     *
     * @param chatMessageStorage chatMessageStorage 聊天记录存储
     */
    abstract void onLastMessage(RoomWxqfChatMessageStorage chatMessageStorage);

    /**
     * 收到错误消息
     *
     * @param chatMessageStorage 聊天记录存储
     */
    abstract void onErrorMessage(RoomWxqfChatMessageStorage chatMessageStorage);

    @Override
    public void onError(RoomWxqfChatMessageStorage chatMessageStorage) {
        // 错误消息
        onErrorMessage(chatMessageStorage);
    }
}
