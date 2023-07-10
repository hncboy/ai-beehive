package com.hncboy.beehive.base.resource.email;

import com.hncboy.beehive.base.exception.ServiceException;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/25
 * 邮箱注册登录配置
 */
@Data
public class EmailRegisterLoginConfig {

    private static final String STAR = "*";

    /**
     * 注册跳转验证链接
     */
    private String registerVerificationRedirectUrl;

    /**
     * 注册邮箱验证码过期时间
     */
    private Integer registerVerifyCodeExpireMinutes;

    /**
     * 注册发送的邮件名称
     */
    private String registerTemplateSubject;

    /**
     * 注册允许的邮箱后缀，按逗号分割
     * 星号表示允许全部，为空表示都不允许
     */
    private String registerAllowSuffix;

    /**
     * 是否允许注册
     */
    private Boolean registerEnabled;

    /**
     * 注册登录的邮箱后缀，按逗号分割
     * 星号表示允许全部，为空表示都不允许
     */
    private String loginAllowSuffix;

    /**
     * 注册审核是否开启
     */
    private Boolean registerCheckEnabled;

    /**
     * 校验邮箱注册权限
     *
     * @param registerEmail 注册的邮箱
     */
    public void checkRegisterPermission(String registerEmail) {
        // 判断是否允许邮箱注册
        if (BooleanUtil.isFalse(registerEnabled)) {
            throw new ServiceException("系统未开放邮箱注册");
        }

        checkSuffixPermission(registerEmail, registerAllowSuffix, "该邮箱后缀不允许注册，限制注册的邮箱后缀 {}");
    }

    /**
     * 校验邮箱登录权限
     *
     * @param registerEmail 注册的邮箱
     */
    public void checkLoginPermission(String registerEmail) {
        checkSuffixPermission(registerEmail, loginAllowSuffix, "该邮箱后缀不允许登录，限制登录的邮箱后缀 {}");
    }

    /**
     * 校验邮箱后缀权限
     *
     * @param email        邮箱
     * @param suffixString 后缀字符串
     * @param errorMessage 错误消息模板
     */
    private void checkSuffixPermission(String email, String suffixString, String errorMessage) {
        if (StrUtil.isBlank(suffixString)) {
            throw new ServiceException("系统未配置允许的后缀");
        }

        List<String> allowSuffixList = StrUtil.split(suffixString, StrPool.COMMA);
        if (allowSuffixList.contains(STAR)) {
            return;
        }

        String[] allowSuffixArray = StrUtil.splitToArray(suffixString, StrPool.COMMA);
        if (StrUtil.endWithAny(email, allowSuffixArray)) {
            return;
        }

        throw new ServiceException(StrUtil.format(errorMessage, allowSuffixList));
    }
}
