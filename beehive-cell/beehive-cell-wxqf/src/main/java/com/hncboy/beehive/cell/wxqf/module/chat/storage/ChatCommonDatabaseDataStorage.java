package com.hncboy.beehive.cell.wxqf.module.chat.storage;

import cn.hutool.core.collection.CollectionUtil;
import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomWxqfChatMsgStatusEnum;
import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonResponse;
import com.hncboy.beehive.cell.wxqf.service.RoomWxqfChatMsgService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/7/24
 * 对话通用数据库数据存储
 */
@Component
public class ChatCommonDatabaseDataStorage extends AbstractDatabaseDataStorage {

    @Resource
    private RoomWxqfChatMsgService roomOpenAiChatMsgService;

    @Override
    public void onFirstMessage(RoomWxqfChatMessageStorage chatMessageStorage) {
        RoomWxqfChatMsgDO questionMessage = chatMessageStorage.getQuestionMessageDO();
        RoomWxqfChatMsgDO answerMessage = new RoomWxqfChatMsgDO();
        answerMessage.setContent(chatMessageStorage.getReceivedMessage());
        answerMessage.setStatus(RoomWxqfChatMsgStatusEnum.PART_SUCCESS);
        // 保存回答消息
        saveAnswerMessage(answerMessage, questionMessage, chatMessageStorage);

        // 设置回答消息
        chatMessageStorage.setAnswerMessageDO(answerMessage);

        // 填充使用 token
        populateMessageUsageToken(chatMessageStorage);

        // 更新问题消息状态为部分成功
        questionMessage.setStatus(RoomWxqfChatMsgStatusEnum.PART_SUCCESS);
        roomOpenAiChatMsgService.updateById(questionMessage);
    }

    @Override
    void onLastMessage(RoomWxqfChatMessageStorage chatMessageStorage) {
        RoomWxqfChatMsgDO questionMessage = chatMessageStorage.getQuestionMessageDO();
        RoomWxqfChatMsgDO answerMessage = chatMessageStorage.getAnswerMessageDO();

        // 成功状态
        questionMessage.setStatus(RoomWxqfChatMsgStatusEnum.COMPLETE_SUCCESS);
        answerMessage.setStatus(RoomWxqfChatMsgStatusEnum.COMPLETE_SUCCESS);

        // 原始响应数据
        answerMessage.setOriginalData(ObjectMapperUtil.toJson(chatMessageStorage.getApiCommonResponses()));
        answerMessage.setContent(chatMessageStorage.getReceivedMessage());

        // 填充使用 token
        populateMessageUsageToken(chatMessageStorage);

        // 更新消息
        roomOpenAiChatMsgService.updateById(questionMessage);
        roomOpenAiChatMsgService.updateById(answerMessage);
    }

    @Override
    void onErrorMessage(RoomWxqfChatMessageStorage chatMessageStorage) {
        // 响应条数大于 0 表示部分成功
        LinkedList<WxqfChatApiCommonResponse> apiCommonResponses = chatMessageStorage.getApiCommonResponses();
        RoomWxqfChatMsgStatusEnum roomWxqfChatMsgStatusEnum = CollectionUtil.isNotEmpty(apiCommonResponses) ? RoomWxqfChatMsgStatusEnum.PART_SUCCESS : RoomWxqfChatMsgStatusEnum.ERROR;

        // 填充问题消息记录
        RoomWxqfChatMsgDO questionMessage = chatMessageStorage.getQuestionMessageDO();
        questionMessage.setStatus(roomWxqfChatMsgStatusEnum);
        // 填充问题错误响应数据
        questionMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());

        // 填充使用 token
        populateMessageUsageToken(chatMessageStorage);

        // 还没收到回复就断了，跳过回答消息记录更新
        if (roomWxqfChatMsgStatusEnum != RoomWxqfChatMsgStatusEnum.ERROR) {
            // 填充问题消息记录
            RoomWxqfChatMsgDO answerMessage = chatMessageStorage.getAnswerMessageDO();
            answerMessage.setStatus(roomWxqfChatMsgStatusEnum);
            // 原始响应数据
            answerMessage.setOriginalData(ObjectMapperUtil.toJson(chatMessageStorage.getApiCommonResponses()));
            // 错误响应数据
            answerMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());
            answerMessage.setContent(chatMessageStorage.getReceivedMessage());
            // 更新错误的回答消息记录
            roomOpenAiChatMsgService.updateById(answerMessage);
        } else {
            // 保存回答消息
            RoomWxqfChatMsgDO answerMessage = new RoomWxqfChatMsgDO();
            answerMessage.setStatus(RoomWxqfChatMsgStatusEnum.ERROR);
            // 错误响应数据
            answerMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());
            // 解析内容填充
            answerMessage.setContent(chatMessageStorage.getParser().parseErrorMessage(answerMessage.getResponseErrorData()));

            // 返回给前端的错误信息从这里取
            chatMessageStorage.setAnswerMessageDO(answerMessage);
            chatMessageStorage.setReceivedMessage(answerMessage.getContent());
            saveAnswerMessage(answerMessage, questionMessage, chatMessageStorage);
        }

        // 更新错误的问题消息记录
        roomOpenAiChatMsgService.updateById(questionMessage);
    }

    /**
     * 保存回答消息
     *
     * @param answerMessage      回答消息
     * @param questionMessage    问题消息
     * @param chatMessageStorage 消息存储
     */
    private void saveAnswerMessage(RoomWxqfChatMsgDO answerMessage, RoomWxqfChatMsgDO questionMessage, RoomWxqfChatMessageStorage chatMessageStorage) {
        answerMessage.setUserId(questionMessage.getUserId());
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setIp(questionMessage.getIp());
        answerMessage.setParentQuestionMessageId(questionMessage.getId());
        answerMessage.setMessageType(MessageTypeEnum.ANSWER);
        answerMessage.setModelName(questionMessage.getModelName());
        answerMessage.setOriginalData(ObjectMapperUtil.toJson(chatMessageStorage.getApiCommonResponses()));
        answerMessage.setPromptTokens(questionMessage.getPromptTokens());
        // 保存回答消息
        roomOpenAiChatMsgService.save(answerMessage);
    }

    /**
     * 填充消息使用 Token 数量
     *
     * @param chatMessageStorage 聊天消息数据存储
     */
    private void populateMessageUsageToken(RoomWxqfChatMessageStorage chatMessageStorage) {
        RoomWxqfChatMsgDO answerMessageDO = chatMessageStorage.getAnswerMessageDO();
        if (Objects.isNull(answerMessageDO)) {
            return;
        }

        LinkedList<WxqfChatApiCommonResponse> apiCommonResponses = chatMessageStorage.getApiCommonResponses();
        Integer promptTokens = 0;
        Integer completionTokens = 0;
        Integer totalTokens = 0;

        for (WxqfChatApiCommonResponse apiCommonResponse : apiCommonResponses) {
            WxqfChatApiCommonResponse.Usage usage = apiCommonResponse.getUsage();
            promptTokens += usage.getPromptTokens();
            completionTokens += usage.getCompletionTokens();
            totalTokens += usage.getTotalTokens();
        }

        answerMessageDO.setPromptTokens(promptTokens);
        answerMessageDO.setCompletionTokens(completionTokens);
        answerMessageDO.setTotalTokens(totalTokens);
    }
}
