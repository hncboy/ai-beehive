package com.hncboy.chatgpt.front.api.parser;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.api.accesstoken.ConversationResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hncboy
 * @date 2023-3-24
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
        // 不为 JSON 直接返回 null，不知道什么情况触发，但是不属于正文
        if (!JSONUtil.isTypeJSON(originalData)) {
            return null;
        }
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
}
