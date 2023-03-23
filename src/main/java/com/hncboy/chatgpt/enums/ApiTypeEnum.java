package com.hncboy.chatgpt.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/3/22 22:19
 * API 类型枚举
 */
@AllArgsConstructor
public enum ApiTypeEnum {

    /**
     * API_KEY
     */
    API_KEY("ChatGPTAPI"),

    /**
     * ACCESS_TOKEN
     */
    ACCESS_TOKEN("ChatGPTUnofficialProxyAPI");

    @Getter
    @JsonValue
    private final String name;
}
