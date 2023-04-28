package com.hncboy.chatgpt.base.handler.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author hncboy
 * @date 2023-3-23
 * 结果状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {

    /**
     * 操作成功
     */
    SUCCESS(HttpServletResponse.SC_OK, "操作成功"),

    /**
     * 业务异常
     */
    FAILURE(HttpServletResponse.SC_BAD_REQUEST, "业务异常"),

    /**
     * 请求未授权
     */
    UN_AUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "请求未授权"),

    /**
     * 服务器异常
     */
    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器异常");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;
}
