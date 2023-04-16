package com.hncboy.chatgpt.base.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邮箱参数配置
 *
 * @author CoDeleven
 */
@Data
@Component
@ConfigurationProperties(prefix = "email")
public class EmailConfig {

    /**
     * 邮箱服务器地址
     */
    private String host;

    /**
     * 邮箱服务器端口，默认25
     */
    private String port;

    /**
     * 发件邮箱地址
     */
    private String from;

    /**
     * 发件人名称
     */
    private String user;

    /**
     * 授权码
     */
    private String pass;

    /**
     * 是否需要授权
     */
    private Boolean auth;

    /**
     * 用于跳转验证的URL模板
     */
    private String verificationRedirectUrl;

    /**
     * 邮箱验证码过期时间，单位分钟。默认15分钟
     */
    private Integer verifyCodeExpireMinutes;

    /**
     * 用于判断邮箱服务是否可用，如果不可用，和邮箱相关的一些功能需要报错
     *
     * @return true有效；false无效
     */
    public boolean isAvailable() {
        return StrUtil.isNotBlank(host) && StrUtil.isNotBlank(port) && StrUtil.isNotBlank(from)
                && StrUtil.isNotBlank(user);
    }
}
