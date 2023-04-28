package com.hncboy.chatgpt.base.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023-3-25
 * AccessToken 对话模型
 */
@AllArgsConstructor
public enum ConversationModelEnum {

    /**
     * 对应官网 Default 3.5 模型
     */
    DEFAULT_GPT_3_5("text-davinci-002-render-sha"),

    /**
     * 对应官网 Legacy 3.5 模型
     * ChatGPT Plus
     */
    LEGACY_GPT_3_5("text-davinci-002-render-paid"),

    /**
     * 对应官网 GPT-4 模型
     * 目前限制 3 小时 25 条消息
     * ChatGPT Plus
     */
    GPT_4("gpt-4");

    @Getter
    @JsonValue
    private final String name;

    /**
     * name 作为 key，封装为 Map
     */
    public static final Map<String, ConversationModelEnum> NAME_MAP = Stream
            .of(ConversationModelEnum.values())
            .collect(Collectors.toMap(ConversationModelEnum::getName, Function.identity()));
}
