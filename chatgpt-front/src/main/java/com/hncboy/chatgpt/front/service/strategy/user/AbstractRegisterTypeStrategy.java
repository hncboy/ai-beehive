package com.hncboy.chatgpt.front.service.strategy.user;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.exception.ServiceException;
import com.hncboy.chatgpt.front.domain.request.RegisterFrontUserForEmailRequest;
import com.hncboy.chatgpt.front.domain.vo.LoginInfoVO;
import com.hncboy.chatgpt.front.domain.vo.UserInfoVO;

import java.nio.charset.StandardCharsets;

/**
 * 注册类型策略抽象类
 *
 * @author CoDeleven
 */
public abstract class AbstractRegisterTypeStrategy {

    /**
     * 根据注册类型获取逻辑处理策略
     *
     * @param registerType 注册类型
     * @return 策略
     */
    public static AbstractRegisterTypeStrategy findStrategyByRegisterType(FrontUserRegisterTypeEnum registerType) {
        switch (registerType) {
            // 邮箱注册策略
            case EMAIL -> {
                return SpringUtil.getBean(EmailAbstractRegisterStrategy.class);
            }
            case PHONE -> {

            }
        }
        throw new ServiceException(StrUtil.format("暂不支持{}注册逻辑", registerType.getDesc()));
    }

    /**
     * 给原生密码+盐进行加密
     *
     * @return 返回加密后16进制的字符串
     */
    protected String encryptRawPassword(String rawPassword, String salt) {
        return MD5.create().digestHex16(rawPassword + salt, StandardCharsets.UTF_8);
    }

    /**
     * 验证是否是有效的注册载体
     *
     * @param identity 邮箱注册就是邮箱，手机号注册就是手机
     * @return true有效，false无效
     */
    public abstract boolean identityUsed(String identity);

    /**
     * 执行注册逻辑
     *
     * @param request 注册请求
     */
    public abstract void register(RegisterFrontUserForEmailRequest request);

    /**
     * 校验验证码是否通过
     *
     * @param identity   用户账号，可能为空。一般邮箱情况下会为空，手机情况下不为空
     * @param verifyCode 邮箱策略时为邮箱验证码；手机策略时为手机短信验证码
     */
    public abstract void checkVerifyCode(String identity, String verifyCode);

    /**
     * 登录
     *
     * @param username 用户名，可以是手机号、邮箱
     * @param password 短信验证码/密码
     * @return 登录成功后的信息
     */
    public abstract LoginInfoVO login(String username, String password);

    /**
     * 获取登录用户信息
     *
     * @param extraInfoId 绑定信息表ID
     * @return 登录用户信息
     */
    public abstract UserInfoVO getLoginUserInfo(Integer extraInfoId);
}