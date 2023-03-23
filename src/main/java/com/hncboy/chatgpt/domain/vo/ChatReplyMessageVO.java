package com.hncboy.chatgpt.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/3/23 13:53
 * 聊天回复的消息
 */
@Data
@Schema(name = "聊天回复的消息")
public class ChatReplyMessageVO {

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
}
