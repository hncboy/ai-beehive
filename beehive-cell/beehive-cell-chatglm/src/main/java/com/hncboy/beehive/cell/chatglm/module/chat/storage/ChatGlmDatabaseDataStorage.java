package com.hncboy.beehive.cell.chatglm.module.chat.storage;

import cn.hutool.core.util.StrUtil;
import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomChatGlmMsgStatusEnum;
import com.hncboy.beehive.cell.chatglm.service.RoomChatGlmMsgService;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.utils.TikTokensUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-25
 * ApiKey 数据库数据存储
 */
@Component
public class ChatGlmDatabaseDataStorage {

    @Resource
    private RoomChatGlmMsgService roomChatGlmMsgService;

    /**
     * 结束响应
     *
     * @param receivedMessage 接收到消息
     */
    public void onComplete(RoomChatGlmMessageStorage receivedMessage) {
        // 最后一条消息
        onLastMessage(receivedMessage);
    }

    public void onMessage(RoomChatGlmMessageStorage chatMessageStorage) {
        // 处理第一条消息
        if (chatMessageStorage.getCurrentStreamMessageCount() != 1) {
            return;
        }
        // 第一条消息
        onFirstMessage(chatMessageStorage);
    }

    public void onFirstMessage(RoomChatGlmMessageStorage chatMessageStorage) {
        RoomChatGlmMsgDO questionMessage = (RoomChatGlmMsgDO) chatMessageStorage.getQuestionMessageDO();
        RoomChatGlmMsgDO answerMessage = new RoomChatGlmMsgDO();
        answerMessage.setContent(chatMessageStorage.getReceivedMessage());
        answerMessage.setStatus(RoomChatGlmMsgStatusEnum.PART_SUCCESS);
        // 保存回答消息
        saveAnswerMessage(answerMessage, questionMessage, chatMessageStorage);

        // 设置回答消息
        chatMessageStorage.setAnswerMessageDO(answerMessage);

        // 更新问题消息状态为部分成功
        questionMessage.setStatus(RoomChatGlmMsgStatusEnum.PART_SUCCESS);
        roomChatGlmMsgService.updateById(questionMessage);
    }

    public void onLastMessage(RoomChatGlmMessageStorage chatMessageStorage) {
        RoomChatGlmMsgDO questionMessage = (RoomChatGlmMsgDO) chatMessageStorage.getQuestionMessageDO();
        RoomChatGlmMsgDO answerMessage = (RoomChatGlmMsgDO) chatMessageStorage.getAnswerMessageDO();

        // 成功状态
        questionMessage.setStatus(RoomChatGlmMsgStatusEnum.COMPLETE_SUCCESS);
        answerMessage.setStatus(RoomChatGlmMsgStatusEnum.COMPLETE_SUCCESS);

        // 原始响应数据
        answerMessage.setOriginalData(chatMessageStorage.getOriginalResponseData());
        answerMessage.setContent(chatMessageStorage.getReceivedMessage());

        // 填充使用 token
        populateMessageUsageToken(chatMessageStorage);

        // 更新消息
        roomChatGlmMsgService.updateById(questionMessage);
        roomChatGlmMsgService.updateById(answerMessage);
    }

    public void onErrorMessage(RoomChatGlmMessageStorage chatMessageStorage) {
        // 消息流条数大于 0 表示部分成功
        RoomChatGlmMsgStatusEnum roomChatGlmMsgStatusEnum = chatMessageStorage.getCurrentStreamMessageCount() > 0 ? RoomChatGlmMsgStatusEnum.PART_SUCCESS : RoomChatGlmMsgStatusEnum.ERROR;

        // 填充问题消息记录
        RoomChatGlmMsgDO questionMessage = (RoomChatGlmMsgDO) chatMessageStorage.getQuestionMessageDO();
        questionMessage.setStatus(roomChatGlmMsgStatusEnum);
        // 填充问题错误响应数据
        questionMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());

        // 填充使用 token
        populateMessageUsageToken(chatMessageStorage);

        // 还没收到回复就断了，跳过回答消息记录更新
        if (roomChatGlmMsgStatusEnum != RoomChatGlmMsgStatusEnum.ERROR) {
            // 填充问题消息记录
            RoomChatGlmMsgDO answerMessage = (RoomChatGlmMsgDO) chatMessageStorage.getAnswerMessageDO();
            answerMessage.setStatus(roomChatGlmMsgStatusEnum);
            // 原始响应数据
            answerMessage.setOriginalData(chatMessageStorage.getOriginalResponseData());
            // 错误响应数据
            answerMessage.setResponseErrorData(chatMessageStorage.getErrorResponseData());
            answerMessage.setContent(chatMessageStorage.getReceivedMessage());
            // 更新错误的回答消息记录
            roomChatGlmMsgService.updateById(answerMessage);
        } else {
            // 保存回答消息
            RoomChatGlmMsgDO answerMessage = new RoomChatGlmMsgDO();
            answerMessage.setStatus(RoomChatGlmMsgStatusEnum.ERROR);
            answerMessage.setOriginalData(chatMessageStorage.getOriginalResponseData());
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
        roomChatGlmMsgService.updateById(questionMessage);
    }

    /**
     * 保存回答消息
     *
     * @param answerMessage      回答消息
     * @param questionMessage    问题消息
     * @param chatMessageStorage 消息存储
     */
    private void saveAnswerMessage(RoomChatGlmMsgDO answerMessage, RoomChatGlmMsgDO questionMessage, RoomChatGlmMessageStorage chatMessageStorage) {
        answerMessage.setUserId(questionMessage.getUserId());
        answerMessage.setRoomId(questionMessage.getRoomId());
        answerMessage.setIp(questionMessage.getIp());
        answerMessage.setParentQuestionMessageId(questionMessage.getId());
        answerMessage.setMessageType(MessageTypeEnum.ANSWER);
        answerMessage.setModelName(questionMessage.getModelName());
        answerMessage.setApiKey(questionMessage.getApiKey());
        answerMessage.setOriginalData(chatMessageStorage.getOriginalResponseData());
        answerMessage.setPromptTokens(questionMessage.getPromptTokens());
        // 保存回答消息
        roomChatGlmMsgService.save(answerMessage);
    }

    /**
     * 填充消息使用 Token 数量
     *
     * @param chatMessageStorage 聊天消息数据存储
     */
    private void populateMessageUsageToken(RoomChatGlmMessageStorage chatMessageStorage) {
        Object answerMessageObj = chatMessageStorage.getAnswerMessageDO();
        if (Objects.isNull(answerMessageObj)) {
            return;
        }

        // 获取模型
        RoomChatGlmMsgDO questionMessage = (RoomChatGlmMsgDO) chatMessageStorage.getQuestionMessageDO();

        // 获取回答消耗的 tokens
        RoomChatGlmMsgDO answerMessage = (RoomChatGlmMsgDO) answerMessageObj;
        String answerContent = answerMessage.getContent();
        int completionTokens = StrUtil.isEmpty(answerContent) ? 0 : TikTokensUtil.tokens(ChatCompletion.Model.GPT_3_5_TURBO.getName(), answerContent);

        // 填充使用情况
        int totalTokens = questionMessage.getPromptTokens() + completionTokens;
        answerMessage.setPromptTokens(questionMessage.getPromptTokens());
        answerMessage.setCompletionTokens(completionTokens);
        answerMessage.setTotalTokens(totalTokens);

        questionMessage.setCompletionTokens(completionTokens);
        questionMessage.setTotalTokens(totalTokens);
    }

    public void onError(RoomChatGlmMessageStorage chatMessageStorage) {
        // 错误消息
        onErrorMessage(chatMessageStorage);
    }
}
