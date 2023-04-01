package com.hncboy.chatgpt.front.api.storage;

import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.common.Usage;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * @author hncboy
 * @date 2023/3/25 22:52
 * ApiKey 数据库数据存储
 */
@Component
public class ApiKeyDatabaseDataStorage extends AbstractDatabaseDataStorage {

    @Override
    public void onFirstMessage(ChatMessageStorage chatMessageStorage) {
        // 第一条消息手动生成消息 id 和对话 id
        ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerChatMessageDO();
        answerChatMessageDO.setMessageId(UUID.randomUUID().toString());
        answerChatMessageDO.setConversationId(UUID.randomUUID().toString());
    }

    @Override
    void onLastMessage(ChatMessageStorage chatMessageStorage) {
        populateMessageUsageToken(chatMessageStorage);
    }

    @Override
    void onErrorMessage(ChatMessageStorage chatMessageStorage) {
        populateMessageUsageToken(chatMessageStorage);
    }

    /**
     * 填充消息使用 Token 数量
     *
     * @param chatMessageStorage 聊天消息数据存储
     */
    private void populateMessageUsageToken(ChatMessageStorage chatMessageStorage) {
        ChatCompletionResponse chatCompletionResponse = (ChatCompletionResponse) chatMessageStorage.getParser()
                .parseSuccess(chatMessageStorage.getOriginalResponseData());
        Usage usage = chatCompletionResponse.getUsage();
        if (Objects.nonNull(usage)) {
            // FIXME 没有 usage
            // 填充使用情况
            ChatMessageDO answerChatMessageDO = chatMessageStorage.getAnswerChatMessageDO();
            answerChatMessageDO.setTotalTokens(usage.getTotalTokens());
            answerChatMessageDO.setPromptTokens(usage.getPromptTokens());
            answerChatMessageDO.setCompletionTokens(usage.getCompletionTokens());

            ChatMessageDO questionChatMessageDO = chatMessageStorage.getQuestionChatMessageDO();
            questionChatMessageDO.setTotalTokens(usage.getTotalTokens());
            questionChatMessageDO.setPromptTokens(usage.getPromptTokens());
            questionChatMessageDO.setCompletionTokens(usage.getCompletionTokens());
        }
    }
}
