package com.hncboy.chatgpt.front.api.accesstoken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hncboy.chatgpt.base.enums.ConversationModelEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @author hncboy
 * @date 2023-3-25
 * AccessToken 对话请求参数
 * {
 * 	"action": "next",
 * 	"messages": [{
 * 		"id": "3c9ff357-aa4b-427b-8528-e9a96ac409ad",
 * 		"role": "user",
 * 		"content": {
 * 			"parts": ["hello"],
 * 			"content_type": "text"
 *                }    * 	}],
 * 	"model": "text-davinci-002-render-sha",
 * 	"parent_message_id": "3c9ff357-aa4b-427b-8528-e9a96ac409ad"
 * }
 */
@Data
@Builder
public class ConversationRequest {

    /**
     * action 类型
     */
    private MessageActionTypeEnum action;

    /**
     * 消息数组，为什么是个数组？
     */
    private List<Message> messages;

    /**
     * 模型
     */
    private ConversationModelEnum model;

    /**
     * 父级消息 id
     * 第一条消息父级消息可以为空
     */
    @JsonProperty("parent_message_id")
    private String parentMessageId;

    /**
     * 对话 id
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 对话消息
     */
    @Data
    @Builder
    public static class Message {

        /**
         * 消息 id，手动生成
         */
        private String id;

        /**
         * 角色
         */
        private String role;

        /**
         * 内容
         */
        private Content content;
    }

    @Data
    @Builder
    public static class Content {

        /**
         * 内容类型，不知道干啥
         */
        @JsonProperty("content_type")
        @Builder.Default
        private String contentType = "text";

        /**
         * 真正的 prompt 数组，为什么是个数组
         */
        private List<String> parts;
    }

    /**
     * Message Action Type 枚举
     */
    @AllArgsConstructor
    public enum MessageActionTypeEnum {

        /**
         * 默认
         */
        NEXT("next"),

        /**
         * 不知道干啥的
         */
        VARIANT("variant");

        @Getter
        @JsonValue
        private final String name;
    }
}
