package com.hncboy.chatgpt.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/6/14
 * OpenAI Chat API 模型
 * @see <a href="https://platform.openai.com/docs/models/gpt-3-5"/>
 */
@AllArgsConstructor
public enum ApiKeyModelEnum {

    /**
     * GPT-3.5
     */
    GPT_3_5_TURBO("gpt-3.5-turbo", 4096, ApiKeyModelEnum.GPT_3_5),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k", 16384, ApiKeyModelEnum.GPT_3_5),
    GPT_3_5_TURBO_0613("gpt-3.5-turbo-0613", 4096, ApiKeyModelEnum.GPT_3_5),
    GPT_3_5_TURBO_16k_0613("gpt-3.5-turbo-16k-0613", 16384, ApiKeyModelEnum.GPT_3_5),

    /**
     * GPT-4
     */
    GPT_4("gpt-4", 8192, ApiKeyModelEnum.GPT4),
    GPT_4_0613("gpt-4-0613", 8192, ApiKeyModelEnum.GPT4),
    GPT_4_32K("gpt-4-32k", 32768, ApiKeyModelEnum.GPT4),
    GPT_4_32K_0613("gpt-4-32k-0613", 32768, ApiKeyModelEnum.GPT4);

    /**
     * 模型名称
     */
    @Getter
    private final String name;

    /**
     * 最大 token 上限
     */
    @Getter
    private final Integer maxTokens;

    /**
     * 用于 token 计算的模型名称
     */
    @Getter
    private final String calcTokenModelName;

    private static final String GPT_3_5 = "gpt-3.5-turbo";
    private static final String GPT4 = "gpt-4";

    /**
     * name 作为 key，封装为 Map
     */
    public static final Map<String, ApiKeyModelEnum> NAME_MAP = Stream
            .of(ApiKeyModelEnum.values())
            .collect(Collectors.toMap(ApiKeyModelEnum::getName, Function.identity()));

    /**
     * 根据模型名称获取最大 token 上限
     *
     * @param modelName 模型名称
     * @return 最大 token 上限
     */
    public static Integer maxTokens(String modelName) {
        return NAME_MAP.get(modelName).getMaxTokens();
    }
}
