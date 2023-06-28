package com.hncboy.beehive.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/5/11
 * 系统参数 key 枚举
 */
@AllArgsConstructor
public enum SysParamKeyEnum {

    /**
     * 百度 AI 配置
     */
    BAIDU_AIP("baidu-aip"),

    /**
     * 邮箱配置
     */
    EMAIL_CONFIG("email-config"),

    /**
     * 邮箱注册登录配置
     */
    EMAIL_REGISTER_LOGIN_CONFIG("email-registerLoginConfig");

    /**
     * paramKey
     */
    @Getter
    private final String paramKey;
}
