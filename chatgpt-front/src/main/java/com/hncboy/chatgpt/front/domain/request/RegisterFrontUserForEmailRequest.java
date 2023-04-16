package com.hncboy.chatgpt.front.domain.request;

import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.front.handler.validation.annotation.FrontUserRegisterAvailable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端用户注册请求
 *
 * @author CoDeleven
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FrontUserRegisterAvailable
@Schema(title = "前端用户注册请求，适用于邮箱登录登录")
public class RegisterFrontUserForEmailRequest {

    @Size(min = 6, max = 64, message = "用户名长度应为6~64个字符")
    @NotNull
    @Schema(title = "用户ID，可以为邮箱，可以为手机号")
    private String identity;

    /**
     * TODO 正则校验
     */
    @Schema(title = "密码")
    @NotNull(message = "密码不能为空")
    private String password;

    @Schema(title = "图形验证码会话 ID")
    @NotNull(message = "验证码会话 ID 不能为空")
    private String picCodeSessionId;

    @Schema(title = "图片验证码")
    @NotNull(message = "图片验证码不能为空")
    private String picVerificationCode;

    private FrontUserRegisterTypeEnum registerType = FrontUserRegisterTypeEnum.EMAIL;
}