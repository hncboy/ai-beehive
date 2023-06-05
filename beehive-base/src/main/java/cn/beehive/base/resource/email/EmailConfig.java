package cn.beehive.base.resource.email;

import cn.beehive.base.cache.SysParamCache;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 邮箱参数配置
 *
 * @author CoDeleven
 */
@Slf4j
@Data
@Configuration
public class EmailConfig implements InitializingBean {

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
     * 跳转验证链接
     */
    private String verificationRedirectUrl;

    /**
     * 邮箱验证码过期时间
     */
    private Integer verifyCodeExpireMinutes;

    /**
     * 邮箱账户
     */
    private MailAccount mailAccount;

    @Override
    public void afterPropertiesSet() {
        // 初始化邮箱参数
        initEmailParam();

        if (StrUtil.isAllNotBlank(host, String.valueOf(port), from, user, verificationRedirectUrl, verifyCodeExpireMinutes.toString())) {
            log.info("邮箱配置初始化成功：{}", this);
        } else {
            throw new RuntimeException("邮箱配置初始化失败");
        }

        // 初始化 MailAccount
        initMailAccount();
    }

    /**
     * 初始化 MailAccount
     */
    private void initMailAccount() {
        mailAccount = new MailAccount();
        mailAccount.setHost(this.host);
        mailAccount.setPort(this.port);
        mailAccount.setFrom(this.from);
        mailAccount.setUser(this.user);
        mailAccount.setAuth(this.auth);
        mailAccount.setDebug(false);
        mailAccount.setSslEnable(true);
        mailAccount.setPass(this.pass);
    }

    /**
     * 初始化邮箱参数
     */
    private void initEmailParam() {
        List<String> paramKeys = new ArrayList<>();
        paramKeys.add(EmailParamConstant.HOST);
        paramKeys.add(EmailParamConstant.PORT);
        paramKeys.add(EmailParamConstant.FROM);
        paramKeys.add(EmailParamConstant.USER);
        paramKeys.add(EmailParamConstant.PASS);
        paramKeys.add(EmailParamConstant.AUTH);
        paramKeys.add(EmailParamConstant.VERIFICATION_REDIRECT_URL);
        paramKeys.add(EmailParamConstant.VERIFY_CODE_EXPIRE_MINUTES);
        Map<String, String> sysParamMap = SysParamCache.multiGet(paramKeys);

        // 项目启动时初始化，如果要修改配置，需要重启项目
        this.host = sysParamMap.get(EmailParamConstant.HOST);
        this.port = Integer.valueOf(sysParamMap.get(EmailParamConstant.PORT));
        this.from = sysParamMap.get(EmailParamConstant.FROM);
        this.user = sysParamMap.get(EmailParamConstant.USER);
        this.pass = sysParamMap.get(EmailParamConstant.PASS);
        this.auth = BooleanUtil.toBoolean(sysParamMap.get(EmailParamConstant.AUTH));
        this.verificationRedirectUrl = sysParamMap.get(EmailParamConstant.VERIFICATION_REDIRECT_URL);
        this.verifyCodeExpireMinutes = Integer.valueOf(sysParamMap.get(EmailParamConstant.VERIFY_CODE_EXPIRE_MINUTES));
    }

    /**
     * 邮箱参数常量
     */
    interface EmailParamConstant {

        String PREFIX = "email-";
        String HOST = PREFIX + "host";
        String PORT = PREFIX + "port";
        String FROM = PREFIX + "from";
        String USER = PREFIX + "user";
        String PASS = PREFIX + "pass";
        String AUTH = PREFIX + "auth";
        String VERIFICATION_REDIRECT_URL = PREFIX + "verificationRedirectUrl";
        String VERIFY_CODE_EXPIRE_MINUTES = PREFIX + "verifyCodeExpireMinutes";
    }
}
