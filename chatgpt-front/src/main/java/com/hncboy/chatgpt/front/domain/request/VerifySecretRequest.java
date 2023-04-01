package com.hncboy.chatgpt.front.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author hncboy
 * @date 2023/3/22 23:03
 * 验证密码请求参数
 */
@Data
@Schema(title = "验证密码请求参数")
public class VerifySecretRequest {

    @NotNull(message = "授权密码不能为空")
    @Schema(title = "授权密码")
    private String token;
}
