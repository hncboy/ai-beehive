package com.hncboy.beehive.cell.openai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/5/31
 * 填写注释
 */
@AllArgsConstructor
public enum OpenAiChatWebModeEnum {

    /**
     * 对应官网 GPT 3.5 模型
     */
    DEFAULT_GPT_3_5("text-davinci-002-render-sha"),

    /**
     * 对应官网 GPT-4 Default
     * 目前限制 3 小时 25 条消息，超过限制报什么错还没测试
     * ChatGPT Plus
     */
    GPT_4("gpt-4");

    @Getter
    private final String name;

    /**
     * name 作为 key，封装为 Map
     */
    public static final Map<String, OpenAiChatWebModeEnum> NAME_MAP = Stream
            .of(OpenAiChatWebModeEnum.values())
            .collect(Collectors.toMap(OpenAiChatWebModeEnum::getName, Function.identity()));
}
