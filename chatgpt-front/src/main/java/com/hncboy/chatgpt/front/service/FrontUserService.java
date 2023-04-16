package com.hncboy.chatgpt.front.service;

import cn.dev33.satoken.util.SaResult;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.handler.response.R;
import com.hncboy.chatgpt.front.domain.request.RegisterFrontUserForEmailRequest;
import com.hncboy.chatgpt.front.domain.vo.LoginInfoVO;
import com.hncboy.chatgpt.front.domain.vo.RegisterCaptchaVO;

/**
 * 前端用户服务，提供注册、认证、授权等功能服务
 *
 * @author CoDeleven
 */
public interface FrontUserService {

    /**
     * 处理注册请求
     *
     * @param request 注册请求
     * @return 注册结果
     */
    R<Boolean> register(RegisterFrontUserForEmailRequest request);

    /**
     * 验证码验证
     *
     * @param frontUserRegisterTypeEnum 注册类型
     * @param code 验证码，可以是邮箱也可以是手机
     * @return true验证成功；false验证失败
     */
    Boolean verifyCode(FrontUserRegisterTypeEnum frontUserRegisterTypeEnum, String code);

    /**
     * 执行登录
     *
     * @param registerType 注册类型
     * @param username 登录用户名，可以是邮箱，可以是手机
     * @param password 登录口令
     * @return Sa-Token的登录结果
     */
    SaResult login(FrontUserRegisterTypeEnum registerType, String username, String password);

    /**
     * 根据注册类型+其他绑定信息表获取该用户的登录信息
     *
     * @param registerType 注册类型
     * @param extraInfoId 对应注册类型的表ID
     * @return 登录用户信息
     */
    LoginInfoVO getUserInfo(FrontUserRegisterTypeEnum registerType, Integer extraInfoId);

    /**
     * 根据当前登录的状态获取用户信息
     *
     * @return 登录的用户信息
     */
    LoginInfoVO getLoginUserInfo();

    /**
     * 生成基于Base64的图形验证码
     *
     * @return Base64图形验证码
     */
    RegisterCaptchaVO generateCaptcha();
}
