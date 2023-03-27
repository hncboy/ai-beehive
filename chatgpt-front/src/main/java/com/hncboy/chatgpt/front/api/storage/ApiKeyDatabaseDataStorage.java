package com.hncboy.chatgpt.front.api.storage;

import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author hncboy
 * @date 2023/3/25 22:52
 * ApiKey 数据库数据存储
 */
@Component
public class ApiKeyDatabaseDataStorage extends AbstractDatabaseDataStorage {

    @Override
    public void onFirstMessage(ChatMessageDO answerChatMessageDO, ChatMessageStorage chatMessageStorage) {
        // 第一条消息手动生成消息 id 和对话 id
        answerChatMessageDO.setMessageId(UUID.randomUUID().toString());
        answerChatMessageDO.setConversationId(UUID.randomUUID().toString());

        // 填充消息使用 Token 数量
        populateMessageUsageToken(answerChatMessageDO, chatMessageStorage);

        // 这里把问题消息记录的 Token 数量也填了
        populateMessageUsageToken(chatMessageStorage.getQuestionChatMessageDO(), chatMessageStorage);
    }

    /**
     * 填充消息使用 Token 数量
     *
     * @param chatMessageDO      聊天消息
     * @param chatMessageStorage 聊天消息数据存储
     */
    private void populateMessageUsageToken(ChatMessageDO chatMessageDO, ChatMessageStorage chatMessageStorage) {
        ChatCompletionResponse chatCompletionResponse = (ChatCompletionResponse) chatMessageStorage.getParser()
                .parseSuccess(chatMessageStorage.getOriginalResponseData());
        // TODO 填充 usage 是否为空判断
//        Usage usage = chatCompletionResponse.getUsage();
//        chatMessageDO.setTotalTokens(usage.getTotalTokens());
//        chatMessageDO.setPromptTokens(usage.getPromptTokens());
//        chatMessageDO.setCompletionTokens(usage.getCompletionTokens());
    }
}
