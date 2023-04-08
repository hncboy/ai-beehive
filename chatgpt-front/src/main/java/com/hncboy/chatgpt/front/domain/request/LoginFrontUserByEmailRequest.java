package com.hncboy.chatgpt.front.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端用户登录请求
 *
 * @author CoDeleven
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(title = "前端用户登录请求")
public class LoginFrontUserByEmailRequest {
    @Schema(title = "邮箱地址")
    private String username;

    @Schema(title = "密码")
    private String password;
}
