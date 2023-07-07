package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/6/30
 * OpenAi ApiKey 状态枚举
 */
@AllArgsConstructor
public enum OpenAiApiKeyStatusEnum {

    /**
     * 启用
     */
    ENABLE("enable"),

    /**
     * 禁用
     * 一般表示暂时不想使用，但是作为启用也是可以访问的
     */
    DISABLE("disable"),

    /**
     * 失效
     * 一般表示账号封禁、余额不足等
     */
    INVALID("invalid");

    @EnumValue
    @Getter
    private final String code;
}
