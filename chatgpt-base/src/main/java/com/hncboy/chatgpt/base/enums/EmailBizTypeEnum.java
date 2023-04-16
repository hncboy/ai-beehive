package com.hncboy.chatgpt.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮箱业务用途枚举
 *
 * @author CoDeleven
 */
@AllArgsConstructor
public enum EmailBizTypeEnum {

    /**
     * 用户注册验证码认证
     */
    REGISTER_VERIFY(10, "注册认证"),

    /**
     * 用户找回密码验证码认证
     */
    RETRIEVE_PASSWORD(11, "找回密码认证");

    @Getter
    @EnumValue
    private final Integer code;

    @Getter
    private final String desc;
}
