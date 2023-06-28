package com.hncboy.beehive.cell.bing.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023/5/30
 * NewBing 模式枚举
 */
@AllArgsConstructor
public enum BingModeEnum {

    /**
     * 均衡模式
     */
    BALANCE("balance", 200),

    /**
     * 准确模式
     */
    PRECISE("precise", 4000),

    /**
     * 创造模式
     */
    CREATIVE("creative", 4000);

    @Getter
    private final String name;

    /**
     * 限制的字数
     */
    @Getter
    private final Integer limitWords;

    /**
     * name 作为 key，封装为 Map
     */
    public static final Map<String, BingModeEnum> NAME_MAP = Stream
            .of(BingModeEnum.values())
            .collect(Collectors.toMap(BingModeEnum::getName, Function.identity()));

}
