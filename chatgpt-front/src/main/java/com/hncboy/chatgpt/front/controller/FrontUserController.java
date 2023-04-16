package com.hncboy.chatgpt.front.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.handler.response.R;
import com.hncboy.chatgpt.front.domain.request.LoginFrontUserByEmailRequest;
import com.hncboy.chatgpt.front.domain.request.RegisterFrontUserForEmailRequest;
import com.hncboy.chatgpt.base.annotation.FrontSaCheckLogin;
import com.hncboy.chatgpt.front.domain.vo.LoginInfoVO;
import com.hncboy.chatgpt.front.domain.vo.RegisterCaptchaVO;
import com.hncboy.chatgpt.front.domain.vo.UserInfoVO;
import com.hncboy.chatgpt.front.service.FrontUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 前端用户控制器
 *
 * @author CoDeleven
 */
@AllArgsConstructor
@Tag(name = "用户相关接口")
@RestController
@RequestMapping("/user")
public class FrontUserController {

    @Resource
    private FrontUserService frontUserService;

    /**
     * 用户收到邮件后，点击认证链接完成认证
     *
     * @param code 认证代码
     * @return 发送结果
     */
    @Operation(summary = "邮件验证回调")
    @GetMapping("/verify_email_code")
    public R<Boolean> verifyEmailCode(@Parameter(description = "邮箱验证码") @RequestParam("code") String code) {
        Boolean data = frontUserService.verifyCode(FrontUserRegisterTypeEnum.EMAIL, code);
        return R.data(data);
    }

    /**
     * 注册前端用户邮箱注册
     *
     * @param request 注册请求
     * @return 发送结果
     */
    @Operation(summary = "使用邮箱进行注册")
    @PostMapping("/register/email")
    public R<Boolean> registerFrontUser(@Validated @RequestBody RegisterFrontUserForEmailRequest request) {
        return frontUserService.register(request);
    }

    /**
     * 获取用户信息
     *
     * @return 登录的用户信息
     */
    @Operation(summary = "获取前端登录的用户信息")
    @GetMapping("/info")
    @FrontSaCheckLogin
    public R<UserInfoVO> getUserInfo() {
        UserInfoVO userInfo = frontUserService.getLoginUserInfo();
        return R.data(userInfo);
    }

    /**
     * 获取图片验证码
     *
     * @return 图片验证码，Base64
     */
    @Operation(summary = "获取图片验证码")
    @GetMapping("/get_pic_code")
    public R<RegisterCaptchaVO> getPictureVerificationCode() {
        RegisterCaptchaVO captcha = frontUserService.generateCaptcha();
        return R.data(captcha);
    }

    /**
     * 前端用户登录
     *
     * @return Sa-Token登录结果
     */
    @Operation(summary = "使用邮箱进行登录")
    @PostMapping("/login/email")
    public R<LoginInfoVO> login(@RequestBody LoginFrontUserByEmailRequest request) {
        LoginInfoVO loginInfo = frontUserService.login(FrontUserRegisterTypeEnum.EMAIL, request.getUsername(), request.getPassword());
        return R.data(loginInfo);
    }
}
