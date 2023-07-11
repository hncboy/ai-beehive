package com.hncboy.beehive.cell.openai.module.chat.storage;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatWebMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.cell.openai.module.chat.accesstoken.ChatWebConversationResponse;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiChatWebMsgService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023-3-25
 * AccessToken 数据库数据存储
 */
@Component
public class AccessTokenDatabaseDataStorage extends AbstractDatabaseDataStorage {

    @Resource
    private RoomOpenAiChatWebMsgService roomOpenAiChatWebMsgService;

    @Override
    public void onFirstMessage(RoomOpenAiChatMessageStorage chatMessageStorage) {
        // 第一条消息
        ChatWebConversationResponse conversationResponse = (ChatWebConversationResponse) chatMessageStorage.getParser().
                parseSuccess(chatMessageStorage.getOriginalResponseData());
        ChatWebConversationResponse.Message message = conversationResponse.getMessage();
        String conversationId = conversationResponse.getConversationId();

        // 填充并更新问题消息的对话 id
        RoomOpenAiChatWebMsgDO questionMessage = (RoomOpenAiChatWebMsgDO) chatMessageStorage.getQuestionMessageDO();
        questionMessage.setRequestConversationId(conversationId);
        roomOpenAiChatWebMsgService.update(new RoomOpenAiChatWebMsgDO(), new LambdaUpdateWrapper<RoomOpenAiChatWebMsgDO>()
                .set(RoomOpenAiChatWebMsgDO::getStatus, RoomOpenAiChatMsgStatusEnum.PART_SUCCESS)
                .set(RoomOpenAiChatWebMsgDO::getRequestConversationId, conversationId)
                .eq(RoomOpenAiChatWebMsgDO::getId, questionMessage.getId()));

        // 回答消息填充请求对话 id 和请求消息 id
        RoomOpenAiChatWebMsgDO answerChatMessage = new RoomOpenAiChatWebMsgDO();
        answerChatMessage.setRequestMessageId(message.getId());
        answerChatMessage.setRequestConversationId(conversationId);
        answerChatMessage.setContent(chatMessageStorage.getReceivedMessage());
        answerChatMessage.setStatus(RoomOpenAiChatMsgStatusEnum.PART_SUCCESS);
        // 保存回答消息
        saveAnswerMessage(answerChatMessage, questionMessage, chatMessageStorage);

        chatMessageStorage.setAnswerMessageDO(answerChatMessage);
    }

    @Override
    void onLastMessage(RoomOpenAiChatMessageStorage chatMessageStorage) {
        RoomOpenAiChatWebMsgDO questionMessage = (RoomOpenAiChatWebMsgDO) chatMessageStorage.getQuestionMessageDO();
        RoomOpenAiChatWebMsgDO answerMessage = (RoomOpenAiChatWebMsgDO) chatMessageStorage.getAnswerMessageDO();

        // 成功状态
        questionMessage.setStatus(RoomOpenAiChatMsgStatusEnum.COMPLETE_SUCCESS);
        answerMessage.setStatus(RoomOpenAiChatMsgStatusEnum.COMPLETE_SUCCESS);

        // 原始响应数据
        answerMessage.setOriginalData(chatMessageStorage.getOriginalResponseData());
        answerMessage.setContent(chatMessageStorage.getReceivedMessage());

        // 更新消息
        roomOpenAiChatWebMsgService.updateById(questionMessage);
        roomOpenAiChatWebMsgService.updateById(answerMessage);
    }

    @Override
    void onErrorMessage(RoomOpenAiChatMessageStorage chatMessageStorage) {
        // 消息流条数大于 0 表示部分成功
        RoomOpenAiChatMsgStatusEnum roomOpenAiChatMsgStatusEnum = chatMessageStorage.getCurrentStreamMessageCount() > 0 ? RoomOpenAiChatMsgStatusEnum.PART_SUCCESS : RoomOpenAiChatMsgStatusEnum.ERROR;

        // 填充问题消息记录
        RoomOpenAiChatWebMsgDO questionMessage = (RoomOpenAiChatWebMsgDO) chatMessageStorage.getQuestionMessageDO();
        questionMessage.setStatus(roomOpenAiChatMsgStatusEnum);
        // 填充问题错误响应数据
        questionMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());

        // 还没收到回复就断了，跳过回答消息记录更新
        if (roomOpenAiChatMsgStatusEnum != RoomOpenAiChatMsgStatusEnum.ERROR) {
            // 填充问题消息记录
            RoomOpenAiChatWebMsgDO answerMessage = (RoomOpenAiChatWebMsgDO) chatMessageStorage.getAnswerMessageDO();
            answerMessage.setStatus(roomOpenAiChatMsgStatusEnum);
            // 原始响应数据
            answerMessage.setOriginalData(chatMessageStorage.getOriginalResponseData());
            // 错误响应数据
            answerMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());
            answerMessage.setContent(chatMessageStorage.getReceivedMessage());
            // 更新错误的回答消息记录
            roomOpenAiChatWebMsgService.updateById(answerMessage);
        } else {
            // 保存回答消息
            RoomOpenAiChatWebMsgDO answerMessage = new RoomOpenAiChatWebMsgDO();
            answerMessage.setStatus(RoomOpenAiChatMsgStatusEnum.ERROR);
            answerMessage.setRequestMessageId("error");
            answerMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());
            answerMessage.setContent(chatMessageStorage.getParser().parseErrorMessage(answerMessage.getResponseErrorData()));

            // 返回给前端的错误信息从这里取
            chatMessageStorage.setAnswerMessageDO(answerMessage);
            chatMessageStorage.setReceivedMessage(answerMessage.getContent());

            saveAnswerMessage(answerMessage, questionMessage, chatMessageStorage);
        }

        // 更新错误的问题消息记录
        roomOpenAiChatWebMsgService.updateById(questionMessage);
    }

    /**
     * 保存回答消息
     *
     * @param answerMessage      回答消息
     * @param questionMessage    问题消息
     * @param chatMessageStorage 消息存储
     */
    private void saveAnswerMessage(RoomOpenAiChatWebMsgDO answerMessage, RoomOpenAiChatWebMsgDO questionMessage, RoomOpenAiChatMessageStorage chatMessageStorage) {
        answerMessage.setUserId(questionMessage.getUserId());
        answerMessage.setRequestConversationId(questionMessage.getRequestConversationId());
        // 请求 parentMessageId 为空的话随机生成一个
        answerMessage.setRequestParentMessageId(questionMessage.getRequestMessageId());
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setIp(questionMessage.getIp());
        answerMessage.setMessageType(MessageTypeEnum.ANSWER);
        answerMessage.setModelName(questionMessage.getModelName());
        answerMessage.setOriginalData(chatMessageStorage.getOriginalResponseData());
        answerMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());
        // 保存回答消息
        roomOpenAiChatWebMsgService.save(answerMessage);
    }
}
