package com.hncboy.chatgpt.domain.request;

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

    @Size(min = 1, max = 2000, message = "提示语字数范围[1, 2000]")
    @Schema(name = "提示语")
    private String prompt;

    @Schema(name = "配置")
    private Options options;

    @Data
    @Schema(name = "消息配置")
    public static class Options {

        @Schema(name = "对话 id")
        private String conversationId;

        @Schema(name = "父级消息 id")
        private String parentMessageId;
    }
}
