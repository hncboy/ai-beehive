package com.hncboy.chatgpt.front.domain.vo;

import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Optional;

/**
 * @author hncboy
 * @date 2023/3/23 13:53
 * 聊天回复的消息
 */
@Data
@Schema(name = "聊天回复的消息")
public class ChatReplyMessageVO {

    /**
     * 对于前端有什么用？
     */
    @Schema(name = "角色")
    private String role;

    @Schema(name = "当前消息 id")
    private String id;

    @Schema(name = "父级消息 id")
    private String parentMessageId;

    @Schema(name = "对话 id")
    private String conversationId;

    @Schema(name = "回复的消息")
    private String text;

    public static ChatReplyMessageVO onEmitterChainException(ChatProcessRequest request) {
        ChatProcessRequest.Options options = request.getOptions();
        ChatReplyMessageVO chatReplyMessageVO = new ChatReplyMessageVO();
        chatReplyMessageVO.setId(Optional.of(options).orElse(new ChatProcessRequest.Options()).getParentMessageId());
        chatReplyMessageVO.setConversationId(Optional.of(options).orElse(new ChatProcessRequest.Options()).getConversationId());
        chatReplyMessageVO.setParentMessageId(null);
        return chatReplyMessageVO;
    }
}
