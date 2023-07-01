package com.hncboy.beehive.cell.openai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/7/1
 * OpenAi ApiKey 策略枚举
 */
@AllArgsConstructor
public enum OpenAiApiKeyStrategyEnum {

    /**
     * 随机
     * 随机一个
     */
    RANDOM("random"),

    /**
     * 权重
     * 取权重最高的一个
     */
    WEIGHT("weight"),

    /**
     * 余额
     * 余额最高的一个
     */
    BALANCE("balance"),

    /**
     * 轮询
     * 按权重排序然后轮询
     */
    POLLING("polling");

    @Getter
    private final String code;

    /**
     * code 作为 key，封装为 Map
     */
    public static final Map<String, OpenAiApiKeyStrategyEnum> CODE_MAP = Stream
            .of(OpenAiApiKeyStrategyEnum.values())
            .collect(Collectors.toMap(OpenAiApiKeyStrategyEnum::getCode, Function.identity()));
}
