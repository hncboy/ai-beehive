package com.hncboy.chatgpt.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hncboy
 * @date 2023-3-28
 * 启用停用枚举类
 */
@AllArgsConstructor
public enum EnableDisableStatusEnum {

    /**
     * 启用
     */
    ENABLE(1),

    /**
     * 停用
     */
    DISABLE(2);

    @Getter
    @EnumValue
    @JsonValue
    private final Integer code;

    /**
     * code 作为 key，封装为 Map
     */
    private static final Map<Integer, EnableDisableStatusEnum> CODE_MAP = Stream
            .of(EnableDisableStatusEnum.values())
            .collect(Collectors.toMap(EnableDisableStatusEnum::getCode, Function.identity()));

    /**
     * 静态工厂反序列化
     *
     * @param key code
     * @return 启用停用枚举
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static EnableDisableStatusEnum valueOfKey(Integer key) {
        return Optional.ofNullable(CODE_MAP.get(key))
                .orElseThrow(() -> new IllegalArgumentException(String.valueOf(key)));
    }
}
