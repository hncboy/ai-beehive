package com.hncboy.chatgpt.front.domain.request;

import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.front.validation.annotation.FrontUserRegisterAvailable;
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

    @Schema(title = "密码信息，邮箱注册时需传入，手机注册时不用传入")
    @NotNull
    private String password;

    @Schema(title = "图形验证码会话ID，必传")
    @NotNull
    private String picCodeSessionId;

    @Schema(title = "图片验证码，必传")
    @NotNull
    private String picVerificationCode;

    private FrontUserRegisterTypeEnum registerType = FrontUserRegisterTypeEnum.EMAIL;
}
