package com.hncboy.chatgpt.admin.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author hncboy
 * @date 2023-3-28
 * 系统用户登录参数
 */
@Schema(title = "系统用户登录参数")
@Data
public class SysUserLoginRequest {

    @NotNull(message = "账号不能为空")
    @Schema(title = "账号")
    private String account;

    @NotNull(message = "密码不能为空")
    @Schema(title = "密码")
    private String password;
}
