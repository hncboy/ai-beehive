package com.hncboy.chatgpt.api.storage;

import com.hncboy.chatgpt.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.enums.ChatMessageStatusEnum;
import com.hncboy.chatgpt.enums.ChatMessageTypeEnum;
import com.hncboy.chatgpt.service.ChatMessageService;
import com.hncboy.chatgpt.service.ChatRoomService;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author hncboy
 * @date 2023/3/25 17:11
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
        answerChatMessageDO.setParentAnswerMessageId(questionChatMessageDO.getParentAnswerMessageId());
        answerChatMessageDO.setParentQuestionMessageId(questionChatMessageDO.getMessageId());
        answerChatMessageDO.setContextCount(questionChatMessageDO.getContextCount());
        answerChatMessageDO.setQuestionContextCount(questionChatMessageDO.getQuestionContextCount());
        answerChatMessageDO.setMessageType(ChatMessageTypeEnum.ANSWER);
        answerChatMessageDO.setChatRoomId(questionChatMessageDO.getChatRoomId());
        answerChatMessageDO.setApiType(questionChatMessageDO.getApiType());
        answerChatMessageDO.setApiKey(questionChatMessageDO.getApiKey());
        answerChatMessageDO.setOriginalData(chatMessageStorage.getOriginalResponseData());
        answerChatMessageDO.setStatus(ChatMessageStatusEnum.PART_SUCCESS);
        answerChatMessageDO.setCreateTime(new Date());
        answerChatMessageDO.setUpdateTime(new Date());

        // 填充第一条消息的字段
        onFirstMessage(answerChatMessageDO, chatMessageStorage);

        // 保存回答消息记录
        chatMessageService.save(answerChatMessageDO);
    }

    /**
     * 收到第一条消息
     *
     * @param answerChatMessageDO 回答消息记录
     * @param chatMessageStorage  聊天记录存储
     */
    public abstract void onFirstMessage(ChatMessageDO answerChatMessageDO, ChatMessageStorage chatMessageStorage);

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

        // 更新消息
        chatMessageService.updateById(questionChatMessageDO);
        chatMessageService.updateById(answerChatMessageDO);
    }

    @Override
    public void onError(ChatMessageStorage chatMessageStorage) {
        // TODO 如果不存在 ChatMessage 应该先插入 没连上就失败会没数据比如 ApiKey 是错误的

        ChatMessageDO questionChatMessageDO = chatMessageStorage.getQuestionChatMessageDO();
        ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerChatMessageDO();

        // 消息流条数大于 0 表示部分成功
        ChatMessageStatusEnum chatMessageStatusEnum = chatMessageStorage.getCurrentStreamMessageCount() > 0 ? ChatMessageStatusEnum.PART_SUCCESS : ChatMessageStatusEnum.ERROR;
        questionChatMessageDO.setStatus(chatMessageStatusEnum);
        answerChatMessageDO.setStatus(chatMessageStatusEnum);

        // 原始请求数据
        questionChatMessageDO.setOriginalData(chatMessageStorage.getOriginalRequestData());

        // 原始响应数据
        answerChatMessageDO.setOriginalData(chatMessageStorage.getOriginalResponseData());

        // 错误响应数据
        questionChatMessageDO.setResponseErrorData(chatMessageStorage.getErrorResponseData());
        answerChatMessageDO.setResponseErrorData(chatMessageStorage.getErrorResponseData());

        // 更新时间
        questionChatMessageDO.setUpdateTime(new Date());
        answerChatMessageDO.setUpdateTime(new Date());

        // 更新消息
        chatMessageService.updateById(questionChatMessageDO);
        chatMessageService.updateById(answerChatMessageDO);
    }
}
