package com.hncboy.beehive.cell.openai.module.chat.accesstoken;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * 		"id": "ffa32afc-2034-4af8-98a0-b3d4a64d66a0",
 * 		"author": {
 * 			"role": "user"
 *                },
 * 		"content": {
 * 			"parts": ["我的上一句是什么"],
 * 			"content_type": "text"
 *        }    * 	}],
 * 	"model": "text-davinci-002-render-sha",
 * 	"parent_message_id": "a011431c-af81-4209-9fb4-76f23655e239",
 * 	"conversation_id": "1d4e146e-5318-4d50-a51d-b671b458ff35",
 * 	"history_and_training_disabled": false
 * }
 */
@Data
@Builder
public class ChatWebConversationRequest {

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
    private String model;

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
         * 作者
         */
        private Author author;

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

    @Data
    @Builder
    public static class Author {

        /**
         * 角色
         */
        private String role;
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
