package com.hncboy.beehive.cell.wxqf.module.chat.api;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话 API 通用请求参数
 */
@Data
public class WxqfChatApiCommonRequest {

    /**
     * 聊天上下文信息。说明：
     * （1）messages 成员不能为空，1个成员表示单轮对话，多个成员表示多轮对话
     * （2）最后一个 message 为当前请求的信息，前面的 message 为历史对话信息
     * （3）必须为奇数个成员，成员中 message 的 role 必须依次为 user、assistant
     * （4）最后一个 message 的 content 长度（即此轮对话的问题）不能超过 2000 个字符；如果 messages 中 content 总长度大于 2000 字符，系统会依次遗忘最早的历史会话，直到 content 的总长度不超过 2000 个字符
     */
    private List<Message> messages;

    /**
     * 是否以流式接口的形式返回数据，默认 false
     */
    private Boolean stream;

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    private String userId;

    @Data
    @Builder
    public static class Message {

        /**
         * 角色
         */
        private Role role;

        /**
         * 对话内容，不能为空
         */
        private String content;
    }

    @AllArgsConstructor
    public enum Role {

        /**
         * 用户
         */
        USER("user"),

        /**
         * 对话助手
         */
        ASSISTANT("assistant");

        @JsonValue
        @Getter
        private final String code;
    }
}
