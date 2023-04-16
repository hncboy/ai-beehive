package com.hncboy.chatgpt.front.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.handler.response.R;
import com.hncboy.chatgpt.base.util.SimpleCaptchaUtil;
import com.hncboy.chatgpt.front.domain.request.RegisterFrontUserForEmailRequest;
import com.hncboy.chatgpt.front.domain.vo.LoginInfoVO;
import com.hncboy.chatgpt.front.domain.vo.RegisterCaptchaVO;
import com.hncboy.chatgpt.front.service.FrontUserService;
import com.hncboy.chatgpt.front.service.strategy.user.RegisterTypeStrategy;
import org.springframework.stereotype.Service;

import static com.hncboy.chatgpt.base.constant.ApplicationConstant.FRONT_JWT_REGISTER_TYPE_CODE;

/**
 * 前端用户服务，用于处理注册/登录/获取用户信息等功能
 *
 * @author CoDeleven
 */
@Service
public class FrontUserServiceImpl implements FrontUserService {

    @Override
    public R<Boolean> register(RegisterFrontUserForEmailRequest request) {
        RegisterTypeStrategy registerStrategy = RegisterTypeStrategy.findStrategyByRegisterType(request.getRegisterType());
        Boolean register = registerStrategy.register(request);
        return R.data(register);
    }

    @Override
    public Boolean verifyCode(FrontUserRegisterTypeEnum registerType, String code) {
        RegisterTypeStrategy registerStrategy = RegisterTypeStrategy.findStrategyByRegisterType(registerType);
        return registerStrategy.checkVerifyCode(null, code);
    }

    @Override
    public SaResult login(FrontUserRegisterTypeEnum registerType, String username, String password) {
        RegisterTypeStrategy strategy = RegisterTypeStrategy.findStrategyByRegisterType(registerType);
        return strategy.login(username, password);
    }

    @Override
    public LoginInfoVO getUserInfo(FrontUserRegisterTypeEnum registerType, Integer extraInfoId) {
        RegisterTypeStrategy strategy = RegisterTypeStrategy.findStrategyByRegisterType(registerType);
        return strategy.getLoginUserInfo(extraInfoId);
    }

    @Override
    public RegisterCaptchaVO generateCaptcha() {
        // 创建一个 图形验证码会话ID
        String picCodeSessionId = IdUtil.fastUUID();
        // 根据图形验证码会话ID获取一个图形验证码。该方法会建立起 验证码会话ID 和 图形验证码的关系
        String captchaBase64Image = SimpleCaptchaUtil.getBase64Captcha(picCodeSessionId);
        // 将验证码会话ID加入到Cookie中
        return RegisterCaptchaVO.builder()
                .picCodeSessionId(picCodeSessionId)
                .picCodeBase64(captchaBase64Image).build();
    }

    @Override
    public LoginInfoVO getLoginUserInfo() {
        // 获取当前登录用户ID，一般是extraInfoId
        Integer extraInfoId = NumberUtil.parseInt(String.valueOf(StpUtil.getLoginId()));
        // 从JWT中提取注册类型
        String registerTypeCode = (String) StpUtil.getExtra(FRONT_JWT_REGISTER_TYPE_CODE);
        // 根据registerCode获取注册类型
        FrontUserRegisterTypeEnum registerType = FrontUserRegisterTypeEnum.getByCode(registerTypeCode);
        return this.getUserInfo(registerType, extraInfoId);
    }
}
