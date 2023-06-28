package com.hncboy.beehive.base.resource.email;

import lombok.Data;

/**
 * 邮箱参数配置
 *
 * @author CoDeleven
 */
@Data
public class EmailConfig  {

    /**
     * SMTP 服务器域名
     */
    private String host;

    /**
     * 邮箱服务器端口
     */
    private Integer port;

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
     * 是否启用 SSL
     */
    private Boolean sslEnable;

    /**
     * 是否启用 STARTTTLS
     */
    private Boolean startttlsEnable;
}
