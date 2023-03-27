package com.hncboy.chatgpt.front.api.storage;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import com.hncboy.chatgpt.front.api.accesstoken.ConversationResponse;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/3/25 23:59
 * AccessToken 数据库数据存储
 */
@Component
public class AccessTokenDatabaseDataStorage extends AbstractDatabaseDataStorage {

    @Override
    public void onFirstMessage(ChatMessageDO answerChatMessageDO, ChatMessageStorage chatMessageStorage) {
        // 第一条消息
        ConversationResponse conversationResponse = (ConversationResponse) chatMessageStorage.getParser().
                parseSuccess(chatMessageStorage.getOriginalResponseData());
        ConversationResponse.Message message = conversationResponse.getMessage();

        // 第一条消息填充对话 id 和消息 id
        answerChatMessageDO.setMessageId(message.getId());
        answerChatMessageDO.setConversationId(conversationResponse.getConversationId());

        // 填充问题消息的对话 id
        chatMessageStorage.getQuestionChatMessageDO().setConversationId(conversationResponse.getConversationId());

        // 聊天室更新 conversationId
        chatRoomService.update(new LambdaUpdateWrapper<ChatRoomDO>()
                .set(ChatRoomDO::getConversationId, answerChatMessageDO.getConversationId())
                .eq(ChatRoomDO::getId, answerChatMessageDO.getChatRoomId()));
    }
}
