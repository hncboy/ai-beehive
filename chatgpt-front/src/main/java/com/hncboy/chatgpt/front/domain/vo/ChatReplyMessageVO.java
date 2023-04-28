package com.hncboy.chatgpt.front.domain.vo;

import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Optional;

/**
 * @author hncboy
 * @date 2023-3-23
 * 聊天回复的消息
 */
@Data
@Schema(title = "聊天回复的消息")
public class ChatReplyMessageVO {

    /**
     * 对于前端有什么用？
     */
    @Schema(title = "角色")
    private String role;

    @Schema(title = "当前消息 id")
    private String id;

    @Schema(title = "父级消息 id")
    private String parentMessageId;

    @Schema(title = "对话 id")
    private String conversationId;

    @Schema(title = "回复的消息")
    private String text;

    /**
     * 当链路出现问题时 取上一条消息的 parentMessageId 和 conversationId，使得异常不影响上下文
     *
     * @param request 消息处理请求的实体 从中获取 parentMessageId 和 conversationId
     * @return 聊天回复的消息
     */
    public static ChatReplyMessageVO onEmitterChainException(ChatProcessRequest request) {
        ChatProcessRequest.Options options = request.getOptions();
        ChatReplyMessageVO chatReplyMessageVO = new ChatReplyMessageVO();
        chatReplyMessageVO.setId(Optional.of(options).orElse(new ChatProcessRequest.Options()).getParentMessageId());
        chatReplyMessageVO.setConversationId(Optional.of(options).orElse(new ChatProcessRequest.Options()).getConversationId());
        chatReplyMessageVO.setParentMessageId(null);
        return chatReplyMessageVO;
    }
}
