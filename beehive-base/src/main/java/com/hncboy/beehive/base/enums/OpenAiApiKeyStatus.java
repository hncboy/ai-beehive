package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/6/30
 * OpenAi ApiKey 状态
 */
@AllArgsConstructor
public enum OpenAiApiKeyStatus {

    /**
     * 启用
     */
    ENABLE("enable"),

    /**
     * 禁用
     */
    DISABLE("disable"),

    /**
     * 失效
     */
    INVALID("invalid");

    @EnumValue
    @Getter
    private final String code;
}
