package com.hncboy.beehive.cell.chatglm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hanpeng
 * @date 2023/8/3
 * ChatGLM 模型
 * @see <a href="https://huggingface.co/THUDM"/>
 */
@AllArgsConstructor
public enum ChatGlmApiModelEnum {

    /**
     * GPT-3.5
     */
    CHATGLM_6B("chatglm-6b", 2048),
    CHATGLM2_6B("chatglm2-6b", 32768),
    CHATGLM2_6B_32K("chatglm2-6b-32k", 32768),
    ;

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
     * name 作为 key，封装为 Map
     */
    public static final Map<String, ChatGlmApiModelEnum> NAME_MAP = Stream
            .of(ChatGlmApiModelEnum.values())
            .collect(Collectors.toMap(ChatGlmApiModelEnum::getName, Function.identity()));

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
