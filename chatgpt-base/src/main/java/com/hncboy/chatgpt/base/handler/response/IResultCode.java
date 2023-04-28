package com.hncboy.chatgpt.base.handler.response;

/**
 * @author hncboy
 * @date 2023-3-23
 * 结果状态码接口
 */
public interface IResultCode {

    /**
     * 获取消息
     *
     * @return 消息
     */
    String getMessage();

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    int getCode();
}
