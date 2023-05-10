package cn.beehive.base.exception;

import cn.beehive.base.handler.response.IResultCode;
import cn.beehive.base.handler.response.ResultCode;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023-3-23
 * 鉴权异常
 */
public class AuthException extends RuntimeException {

    @Getter
    private final IResultCode resultCode;

    public AuthException(String message) {
        super(message);
        this.resultCode = ResultCode.UN_AUTHORIZED;
    }
}
