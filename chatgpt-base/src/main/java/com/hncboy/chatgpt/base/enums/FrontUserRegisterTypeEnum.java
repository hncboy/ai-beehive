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
 * 前端用户注册类型
 *
 * @author CoDeleven
 */
@Getter
@AllArgsConstructor
public enum FrontUserRegisterTypeEnum {

    /**
     * 邮箱注册
     */
    EMAIL("email", "邮箱"),

    /**
     * 手机号注册
     */
    PHONE("phone", "手机号");

    @Getter
    @EnumValue
    @JsonValue
    private final String code;

    @Getter
    private final String desc;

    /**
     * code 作为 key，封装为 Map
     */
    public static final Map<String, FrontUserRegisterTypeEnum> CODE_MAP = Stream
            .of(FrontUserRegisterTypeEnum.values())
            .collect(Collectors.toMap(FrontUserRegisterTypeEnum::getCode, Function.identity()));

    /**
     * 静态工厂反序列化
     *
     * @param code code
     * @return 启用停用枚举
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FrontUserRegisterTypeEnum valueOfKey(String code) {
        return Optional.ofNullable(CODE_MAP.get(code))
                .orElseThrow(() -> new IllegalArgumentException(String.valueOf(code)));
    }
}
