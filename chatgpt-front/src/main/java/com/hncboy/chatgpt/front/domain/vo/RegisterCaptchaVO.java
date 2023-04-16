package com.hncboy.chatgpt.front.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册验证码请求参数
 *
 * @author CoDeleven
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(title = "每次注册前需要获取图形验证码信息")
public class RegisterCaptchaVO {

    @Schema(title = "图形验证码会话ID，注册时需要传过来")
    private String picCodeSessionId;

    @Schema(title = "图形验证码Base64")
    private String picCodeBase64;
}
