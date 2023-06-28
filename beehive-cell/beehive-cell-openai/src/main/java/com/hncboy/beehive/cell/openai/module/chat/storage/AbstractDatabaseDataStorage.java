package com.hncboy.beehive.cell.openai.module.chat.storage;

/**
 * @author hncboy
 * @date 2023-3-25
 * 数据库数据存储抽象类
 */
public abstract class AbstractDatabaseDataStorage implements DataStorage {

    @Override
    public void onMessage(RoomOpenAiChatMessageStorage chatMessageStorage) {
        // 处理第一条消息
        if (chatMessageStorage.getCurrentStreamMessageCount() != 1) {
            return;
        }
        // 第一条消息
        onFirstMessage(chatMessageStorage);
    }

    /**
     * 收到第一条消息
     *
     * @param chatMessageStorage
     * chatMessageStorage 聊天记录存储
     */
    abstract void onFirstMessage(RoomOpenAiChatMessageStorage chatMessageStorage);

    /**
     * 收到最后第一条消息
     *
     * @param chatMessageStorage
     * chatMessageStorage 聊天记录存储
     */
    abstract void onLastMessage(RoomOpenAiChatMessageStorage chatMessageStorage);

    /**
     * 收到错误消息
     *
     * @param chatMessageStorage 聊天记录存储
     */
    abstract void onErrorMessage(RoomOpenAiChatMessageStorage chatMessageStorage);

    @Override
    public void onComplete(RoomOpenAiChatMessageStorage chatMessageStorage) {
        // 最后一条消息
        onLastMessage(chatMessageStorage);
    }

    @Override
    public void onError(RoomOpenAiChatMessageStorage chatMessageStorage) {
        // 错误消息
        onErrorMessage(chatMessageStorage);
    }
}
