package com.hncboy.chatgpt.front.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @author hncboy
 * @date 2023/3/23 13:17
 * 消息处理请求
 */
@Data
@Schema(name = "消息处理请求")
public class ChatProcessRequest {

    @Size(min = 1, max = 2000, message = "问题字数范围[1, 2000]")
    @Schema(title = "问题")
    private String prompt;

    @Schema(title = "配置")
    private Options options;

    @Size(max = 100, message = "系统消息字数范围[0, 2000]")
    @Schema(title = "系统消息")
    private String systemMessage;

    @Data
    @Schema(title = "消息配置")
    public static class Options {

        @Schema(title = "对话 id")
        private String conversationId;

        /**
         * 这里的父级消息指的是回答的父级消息 id
         * 前端发送问题，需要上下文的话传回答的父级消息 id
         */
        @Schema(title = "父级消息 id")
        private String parentMessageId;
    }
}
