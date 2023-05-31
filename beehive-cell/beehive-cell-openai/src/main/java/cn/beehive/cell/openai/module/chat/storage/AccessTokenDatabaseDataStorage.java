/*
package cn.beehive.cell.openai.module.chat.storage;

import cn.beehive.base.domain.entity.ChatMessageDO;
import cn.beehive.base.domain.entity.ChatRoomDO;
import cn.beehive.base.enums.ChatMessageStatusEnum;
import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.cell.openai.module.chat.accesstoken.ConversationResponse;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Component;

import java.util.Date;

*/
/**
 * @author hncboy
 * @date 2023-3-25
 * AccessToken 数据库数据存储
 *//*

@Component
public class AccessTokenDatabaseDataStorage extends AbstractDatabaseDataStorage {

    @Override
    public void onFirstMessage(ChatMessageStorage chatMessageStorage) {

        ChatMessageDO questionChatMessageDO = chatMessageStorage.getQuestionMessageDO();
        ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerMessageDO();
        answerChatMessageDO.setParentMessageId(questionChatMessageDO.getMessageId());
        answerChatMessageDO.setUserId(questionChatMessageDO.getUserId());
        answerChatMessageDO.setParentAnswerMessageId(questionChatMessageDO.getParentAnswerMessageId());
        answerChatMessageDO.setParentQuestionMessageId(questionChatMessageDO.getMessageId());
        answerChatMessageDO.setContextCount(questionChatMessageDO.getContextCount());
        answerChatMessageDO.setQuestionContextCount(questionChatMessageDO.getQuestionContextCount());
        answerChatMessageDO.setModelName(questionChatMessageDO.getModelName());
        answerChatMessageDO.setMessageType(MessageTypeEnum.ANSWER);
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
        // 第一条消息
        ConversationResponse conversationResponse = (ConversationResponse) chatMessageStorage.getParser().
                parseSuccess(chatMessageStorage.getOriginalResponseData());
        ConversationResponse.Message message = conversationResponse.getMessage();

        // 第一条消息填充对话 id 和消息 id
        ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerMessageDO();
        answerChatMessageDO.setMessageId(message.getId());
        answerChatMessageDO.setConversationId(conversationResponse.getConversationId());

        // 填充问题消息的对话 id
        chatMessageStorage.getQuestionMessageDO().setConversationId(conversationResponse.getConversationId());
    }

    @Override
    void onLastMessage(ChatMessageStorage chatMessageStorage) {
        ChatMessageDO questionChatMessageDO = chatMessageStorage.getQuestionMessageDO();
        ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerMessageDO();

        // 成功状态
        questionChatMessageDO.setStatus(ChatMessageStatusEnum.COMPLETE_SUCCESS);
        answerChatMessageDO.setStatus(ChatMessageStatusEnum.COMPLETE_SUCCESS);

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
    void onErrorMessage(ChatMessageStorage chatMessageStorage) {
// 消息流条数大于 0 表示部分成功
        ChatMessageStatusEnum chatMessageStatusEnum = chatMessageStorage.getCurrentStreamMessageCount() > 0 ? ChatMessageStatusEnum.PART_SUCCESS : ChatMessageStatusEnum.ERROR;

        // 填充问题消息记录
        ChatMessageDO questionChatMessageDO = chatMessageStorage.getQuestionMessageDO();
        questionChatMessageDO.setStatus(chatMessageStatusEnum);
        // 原始请求数据
        questionChatMessageDO.setOriginalData(chatMessageStorage.getOriginalRequestData());
        // 错误响应数据
        questionChatMessageDO.setResponseErrorData(chatMessageStorage.getErrorResponseData());
        questionChatMessageDO.setUpdateTime(new Date());

        // 还没收到回复就断了，跳过回答消息记录更新
        if (chatMessageStatusEnum != ChatMessageStatusEnum.ERROR) {
            // 填充问题消息记录
            ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerMessageDO();
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
        chatMessageService.updateById(chatMessageStorage.getQuestionMessageDO());
        // 更新错误的回答消息记录
        if (chatMessageStatusEnum != ChatMessageStatusEnum.ERROR) {
            chatMessageService.updateById(chatMessageStorage.getAnswerMessageDO());
        }
    }
}
*/
