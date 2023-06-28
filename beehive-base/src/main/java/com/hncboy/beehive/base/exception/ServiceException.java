package com.hncboy.beehive.base.exception;

import com.hncboy.beehive.base.handler.response.IResultCode;
import com.hncboy.beehive.base.handler.response.ResultCode;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023-3-23
 * 业务异常
 */
public class ServiceException extends RuntimeException {

    @Getter
    private final IResultCode resultCode;

    public ServiceException(String message) {
        super(message);
        this.resultCode = ResultCode.FAILURE;
    }
}
