package com.hncboy.chatgpt.api.parser;

import cn.hutool.core.collection.CollectionUtil;
import com.hncboy.chatgpt.api.accesstoken.ConversationResponse;
import com.hncboy.chatgpt.domain.vo.ChatReplyMessageVO;
import com.hncboy.chatgpt.util.ObjectMapperUtil;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/24 17:53
 * AccessToken 的聊天对话解析器
 */
@Component
public class AccessTokenChatResponseParser implements ResponseParser<ConversationResponse> {

    @Override
    public ConversationResponse parseSuccess(String originalData) {
        return ObjectMapperUtil.fromJson(originalData, ConversationResponse.class);
    }

    @Override
    public String parseReceivedMessage(String receivedMessage, String newMessage) {
        return newMessage;
    }

    @Override
    public String parseNewMessage(String originalData) {
        ConversationResponse.Message message = parseSuccess(originalData).getMessage();
        ConversationResponse.Author author = message.getAuthor();
        if (!author.getRole().equals(Message.Role.ASSISTANT.getName())) {
            return null;
        }

        // 只需要 role=assistant 的消息
        List<String> parts = message.getContent().getParts();
        if (CollectionUtil.isEmpty(parts)) {
            return null;
        }

        // AccessToken 模式返回的消息每句都会包含前面的话，不需要手动拼接
        return parts.get(0);
    }

    @Override
    public ChatReplyMessageVO parseChatReplyMessageVO(String receivedMessage, String originalData) {
        ConversationResponse conversationResponse = parseSuccess(originalData);
        ConversationResponse.Message message = conversationResponse.getMessage();
        ChatReplyMessageVO chatReplyMessageVO = new ChatReplyMessageVO();
        chatReplyMessageVO.setRole(message.getAuthor().getRole());
        chatReplyMessageVO.setId(message.getId());
        // 当前消息的父级消息 id 应该没有用
        chatReplyMessageVO.setParentMessageId(null);
        chatReplyMessageVO.setConversationId(conversationResponse.getConversationId());
        chatReplyMessageVO.setText(receivedMessage);
        return chatReplyMessageVO;
    }
}
