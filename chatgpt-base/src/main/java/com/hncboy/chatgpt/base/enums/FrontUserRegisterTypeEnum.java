package com.hncboy.chatgpt.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 前端用户注册类型
 *
 * @author CoDeleven
 */
@Getter
@AllArgsConstructor
public enum FrontUserRegisterTypeEnum {
    EMAIL("email", "邮箱"),

    PHONE("phone", "手机号")
    ;

    @Getter
    @EnumValue
    @JsonValue
    private final String code;

    @Getter
    private final String desc;

    /**
     * 根据code获取注册枚举类型
     * @param registerTypeCode 注册代码
     * @return 枚举类型
     */
    public static FrontUserRegisterTypeEnum getByCode(String registerTypeCode) {
        for (FrontUserRegisterTypeEnum type : FrontUserRegisterTypeEnum.values()) {
            if(Objects.equals(type.getCode(), registerTypeCode)) {
                return type;
            }
        }
        return null;
    }
}
