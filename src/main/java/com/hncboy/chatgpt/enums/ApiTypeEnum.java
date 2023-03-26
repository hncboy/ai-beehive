package com.hncboy.chatgpt.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hncboy.chatgpt.handler.emitter.AccessTokenResponseEmitter;
import com.hncboy.chatgpt.handler.emitter.ApiKeyResponseEmitter;
import com.hncboy.chatgpt.handler.emitter.ResponseEmitter;
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
    API_KEY("ApiKey", "ChatGPTAPI", ApiKeyResponseEmitter.class),

    /**
     * ACCESS_TOKEN
     */
    ACCESS_TOKEN("AccessToken", "ChatGPTUnofficialProxyAPI", AccessTokenResponseEmitter.class);

    @Getter
    @EnumValue
    private final String name;

    @Getter
    @JsonValue
    private final String message;

    @Getter
    private final Class<? extends ResponseEmitter> responseEmitterClazz;
}
