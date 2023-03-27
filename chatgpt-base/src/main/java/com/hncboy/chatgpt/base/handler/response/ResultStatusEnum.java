package com.hncboy.chatgpt.base.handler.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/3/23 12:26
 * 响应结果状态枚举
 */
@AllArgsConstructor
public enum ResultStatusEnum {

    /**
     * 成功
     */
    SUCCESS("Success"),

    /**
     * 失败
     */
    FAIL("Fail"),

    /**
     * 未授权
     */
    Unauthorized("Unauthorized");

    @Getter
    @JsonValue
    private final String msg;
}
