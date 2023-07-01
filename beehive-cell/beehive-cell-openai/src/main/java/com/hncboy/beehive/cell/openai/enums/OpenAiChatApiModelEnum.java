package com.hncboy.beehive.cell.openai.enums;

import com.hncboy.beehive.base.enums.OpenAiApiKeyUseSceneEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/6/7
 * OpenAI Chat API 模型
 * @see <a href="https://platform.openai.com/docs/models/gpt-3-5"/>
 */
@AllArgsConstructor
public enum OpenAiChatApiModelEnum {

    /**
     * GPT-3.5
     */
    GPT_3_5_TURBO("gpt-3.5-turbo", 4096, OpenAiChatApiModelEnum.GPT_3_5, OpenAiApiKeyUseSceneEnum.GPT_3_5),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k", 16384, OpenAiChatApiModelEnum.GPT_3_5, OpenAiApiKeyUseSceneEnum.GPT_3_5),
    GPT_3_5_TURBO_0613("gpt-3.5-turbo-0613", 4096, OpenAiChatApiModelEnum.GPT_3_5, OpenAiApiKeyUseSceneEnum.GPT_3_5),
    GPT_3_5_TURBO_16k_0613("gpt-3.5-turbo-16k-0613", 16384, OpenAiChatApiModelEnum.GPT_3_5, OpenAiApiKeyUseSceneEnum.GPT_3_5),

    /**
     * GPT-4
     */
    GPT_4("gpt-4", 8192, OpenAiChatApiModelEnum.GPT4, OpenAiApiKeyUseSceneEnum.GPT_4),
    GPT_4_0613("gpt-4-0613", 8192, OpenAiChatApiModelEnum.GPT4, OpenAiApiKeyUseSceneEnum.GPT_4),
    GPT_4_32K("gpt-4-32k", 32768, OpenAiChatApiModelEnum.GPT4, OpenAiApiKeyUseSceneEnum.GPT_4),
    GPT_4_32K_0613("gpt-4-32k-0613", 32768, OpenAiChatApiModelEnum.GPT4, OpenAiApiKeyUseSceneEnum.GPT_4);

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

    /**
     * 使用场景
     */
    @Getter
    private final OpenAiApiKeyUseSceneEnum useSceneEnum;

    private static final String GPT_3_5 = "gpt-3.5-turbo";
    private static final String GPT4 = "gpt-4";

    /**
     * name 作为 key，封装为 Map
     */
    public static final Map<String, OpenAiChatApiModelEnum> NAME_MAP = Stream
            .of(OpenAiChatApiModelEnum.values())
            .collect(Collectors.toMap(OpenAiChatApiModelEnum::getName, Function.identity()));

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
