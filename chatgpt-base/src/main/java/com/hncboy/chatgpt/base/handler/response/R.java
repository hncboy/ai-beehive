package com.hncboy.chatgpt.base.handler.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hncboy
 * @date 2023/3/22 20:16
 * 响应结果
 */
@Data
@Schema(title = "响应结果")
public class R<T> {

    @Schema(title = "状态码")
    private int code;

    @Schema(title = "状态字符串")
    private ResultStatusEnum status;

    @Schema(title = "承载数据")
    private T data;

    @Schema(title = "返回消息")
    private String message;

    private R(IResultCode resultCode) {
        this(resultCode, null, resultCode.getMessage());
    }

    private R(IResultCode resultCode, String msg) {
        this(resultCode, null, msg);
    }

    private R(IResultCode resultCode, T data) {
        this(resultCode, data, resultCode.getMessage());
    }

    private R(IResultCode resultCode, T data, String msg) {
        this(resultCode.getCode(), data, msg);
    }

    private R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.message = msg;
        this.status = resultCodeToStatus(code);
    }

    private static ResultStatusEnum resultCodeToStatus(int code) {
        ResultStatusEnum status = ResultStatusEnum.SUCCESS;
        if (code != ResultCode.SUCCESS.getCode()) {
            if (code == ResultCode.UN_AUTHORIZED.getCode()) {
                status = ResultStatusEnum.Unauthorized;
            } else {
                status = ResultStatusEnum.FAIL;
            }
        }
        return status;
    }

    /**
     * 返回 R
     *
     * @param data 数据
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> R<T> data(T data) {
        return data(data, HttpStatus.OK.getReasonPhrase());
    }

    /**
     * 返回 R
     *
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> R<T> data(T data, String msg) {
        return data(HttpServletResponse.SC_OK, data, msg);
    }

    /**
     * 返回 R
     *
     * @param code 状态码
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return R
     */
    public static <T> R<T> data(int code, T data, String msg) {
        return new R<>(code, data, msg);
    }

    /**
     * 返回 R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> R<T> success(String msg) {
        return data(null, msg);
    }

    /**
     * 返回 R
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return R
     */
    public static <T> R<T> fail(String msg) {
        return data(HttpServletResponse.SC_BAD_REQUEST, null, msg);
    }

    /**
     * 返回 R
     *
     * @param resultCode 状态码
     * @param msg        消息
     * @param <T>        T 泛型标记
     * @return R
     */
    public static <T> R<T> fail(IResultCode resultCode, String msg) {
        return new R<>(resultCode, msg);
    }
}
