package com.hncboy.chatgpt.base.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023-3-22
 * API 类型枚举
 */
@AllArgsConstructor
public enum ApiTypeEnum {

    /**
     * API_KEY
     */
    API_KEY("ApiKey", "ChatGPTAPI"),

    /**
     * ACCESS_TOKEN
     */
    ACCESS_TOKEN("AccessToken", "ChatGPTUnofficialProxyAPI");

    @Getter
    @EnumValue
    private final String name;

    @Getter
    @JsonValue
    private final String message;
}
