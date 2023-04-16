package com.hncboy.chatgpt.front.service;

import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.front.domain.request.RegisterFrontUserForEmailRequest;
import com.hncboy.chatgpt.front.domain.vo.LoginInfoVO;
import com.hncboy.chatgpt.front.domain.vo.RegisterCaptchaVO;
import com.hncboy.chatgpt.front.domain.vo.UserInfoVO;

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
     */
    void register(RegisterFrontUserForEmailRequest request);

    /**
     * 验证码验证
     *
     * @param frontUserRegisterTypeEnum 注册类型
     * @param code                      验证码，可以是邮箱也可以是手机
     */
    void verifyCode(FrontUserRegisterTypeEnum frontUserRegisterTypeEnum, String code);

    /**
     * 执行登录
     *
     * @param registerType 注册类型
     * @param username     登录用户名，可以是邮箱，可以是手机
     * @param password     登录口令
     * @return Sa-Token的登录结果
     */
    LoginInfoVO login(FrontUserRegisterTypeEnum registerType, String username, String password);

    /**
     * 根据注册类型+其他绑定信息表获取该用户的登录信息
     *
     * @param registerType 注册类型
     * @param extraInfoId  对应注册类型的表ID
     * @return 登录用户信息
     */
    UserInfoVO getUserInfo(FrontUserRegisterTypeEnum registerType, Integer extraInfoId);

    /**
     * 根据当前登录的状态获取用户信息
     *
     * @return 登录的用户信息
     */
    UserInfoVO getLoginUserInfo();

    /**
     * 生成基于 Base64 的图形验证码
     *
     * @return Base64 图形验证码
     */
    RegisterCaptchaVO generateCaptcha();
}
