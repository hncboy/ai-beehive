package com.hncboy.chatgpt.front.api.storage;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import com.hncboy.chatgpt.base.enums.ChatMessageStatusEnum;
import com.hncboy.chatgpt.base.enums.ChatMessageTypeEnum;
import com.hncboy.chatgpt.front.service.ChatMessageService;
import com.hncboy.chatgpt.front.service.ChatRoomService;
import jakarta.annotation.Resource;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023-3-25
 * 数据库数据存储抽象类
 */
public abstract class AbstractDatabaseDataStorage implements DataStorage {

    @Resource
    protected ChatMessageService chatMessageService;

    @Resource
    protected ChatRoomService chatRoomService;

    @Override
    public void onMessage(ChatMessageStorage chatMessageStorage) {
        // 处理第一条消息
        if (chatMessageStorage.getCurrentStreamMessageCount() != 1) {
            return;
        }

        ChatMessageDO questionChatMessageDO = chatMessageStorage.getQuestionChatMessageDO();
        ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerChatMessageDO();
        answerChatMessageDO.setParentMessageId(questionChatMessageDO.getMessageId());
        answerChatMessageDO.setUserId(questionChatMessageDO.getUserId());
        answerChatMessageDO.setParentAnswerMessageId(questionChatMessageDO.getParentAnswerMessageId());
        answerChatMessageDO.setParentQuestionMessageId(questionChatMessageDO.getMessageId());
        answerChatMessageDO.setContextCount(questionChatMessageDO.getContextCount());
        answerChatMessageDO.setQuestionContextCount(questionChatMessageDO.getQuestionContextCount());
        answerChatMessageDO.setModelName(questionChatMessageDO.getModelName());
        answerChatMessageDO.setMessageType(ChatMessageTypeEnum.ANSWER);
        answerChatMessageDO.setChatRoomId(questionChatMessageDO.getChatRoomId());
        answerChatMessageDO.setApiType(questionChatMessageDO.getApiType());
        answerChatMessageDO.setApiKey(questionChatMessageDO.getApiKey());
        answerChatMessageDO.setOriginalData(chatMessageStorage.getOriginalResponseData());
        answerChatMessageDO.setStatus(ChatMessageStatusEnum.PART_SUCCESS);
        answerChatMessageDO.setIp(questionChatMessageDO.getIp());
        answerChatMessageDO.setCreateTime(new Date());
        answerChatMessageDO.setUpdateTime(new Date());

        // 填充第一条消息的字段
        onFirstMessage(chatMessageStorage);

        // 保存回答消息记录
        chatMessageService.save(answerChatMessageDO);

        // 聊天室更新 conversationId
        chatRoomService.update(new LambdaUpdateWrapper<ChatRoomDO>()
                .set(ChatRoomDO::getConversationId, answerChatMessageDO.getConversationId())
                .eq(ChatRoomDO::getId, answerChatMessageDO.getChatRoomId()));
    }

    /**
     * 收到第一条消息
     *
     * @param chatMessageStorage 聊天记录存储
     */
    abstract void onFirstMessage(ChatMessageStorage chatMessageStorage);

    /**
     * 收到最后第一条消息
     *
     * @param chatMessageStorage 聊天记录存储
     */
    abstract void onLastMessage(ChatMessageStorage chatMessageStorage);

    /**
     * 收到错误消息
     *
     * @param chatMessageStorage 聊天记录存储
     */
    abstract void onErrorMessage(ChatMessageStorage chatMessageStorage);

    @Override
    public void onComplete(ChatMessageStorage chatMessageStorage) {
        ChatMessageDO questionChatMessageDO = chatMessageStorage.getQuestionChatMessageDO();
        ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerChatMessageDO();

        // 成功状态
        questionChatMessageDO.setStatus(ChatMessageStatusEnum.COMPLETE_SUCCESS);
        answerChatMessageDO.setStatus(ChatMessageStatusEnum.COMPLETE_SUCCESS);

        // 原始请求数据
        questionChatMessageDO.setOriginalData(chatMessageStorage.getOriginalRequestData());

        // 原始响应数据
        answerChatMessageDO.setOriginalData(chatMessageStorage.getOriginalResponseData());

        // 更新时间
        questionChatMessageDO.setUpdateTime(new Date());
        answerChatMessageDO.setUpdateTime(new Date());

        // 最后一条消息
        onLastMessage(chatMessageStorage);

        // 更新消息
        chatMessageService.updateById(questionChatMessageDO);
        chatMessageService.updateById(answerChatMessageDO);
    }

    @Override
    public void onError(ChatMessageStorage chatMessageStorage) {
        // 消息流条数大于 0 表示部分成功
        ChatMessageStatusEnum chatMessageStatusEnum = chatMessageStorage.getCurrentStreamMessageCount() > 0 ? ChatMessageStatusEnum.PART_SUCCESS : ChatMessageStatusEnum.ERROR;

        // 填充问题消息记录
        ChatMessageDO questionChatMessageDO = chatMessageStorage.getQuestionChatMessageDO();
        questionChatMessageDO.setStatus(chatMessageStatusEnum);
        // 原始请求数据
        questionChatMessageDO.setOriginalData(chatMessageStorage.getOriginalRequestData());
        // 错误响应数据
        questionChatMessageDO.setResponseErrorData(chatMessageStorage.getErrorResponseData());
        questionChatMessageDO.setUpdateTime(new Date());

        // 还没收到回复就断了，跳过回答消息记录更新
        if (chatMessageStatusEnum != ChatMessageStatusEnum.ERROR) {
            // 填充问题消息记录
            ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerChatMessageDO();
            answerChatMessageDO.setStatus(chatMessageStatusEnum);
            // 原始响应数据
            answerChatMessageDO.setOriginalData(chatMessageStorage.getOriginalResponseData());
            // 错误响应数据
            answerChatMessageDO.setResponseErrorData(chatMessageStorage.getErrorResponseData());
            // 更新时间
            answerChatMessageDO.setUpdateTime(new Date());
        }

        // 填充错误消息
        onErrorMessage(chatMessageStorage);

        // 更新错误的问题消息记录
        chatMessageService.updateById(chatMessageStorage.getQuestionChatMessageDO());
        // 更新错误的回答消息记录
        if (chatMessageStatusEnum != ChatMessageStatusEnum.ERROR) {
            chatMessageService.updateById(chatMessageStorage.getAnswerChatMessageDO());
        }
    }
}
