package com.hncboy.chatgpt.front.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 前端用户登录请求
 *
 * @author CoDeleven
 */
@Data
@Schema(title = "前端用户登录请求")
public class LoginFrontUserByEmailRequest {

    @Schema(title = "邮箱地址")
    private String username;

    @Schema(title = "密码")
    private String password;
}
