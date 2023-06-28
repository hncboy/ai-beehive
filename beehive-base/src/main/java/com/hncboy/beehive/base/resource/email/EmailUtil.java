package com.hncboy.beehive.base.resource.email;

import com.hncboy.beehive.base.util.ObjectMapperUtil;
import com.hncboy.beehive.base.cache.SysParamCache;
import com.hncboy.beehive.base.enums.SysParamKeyEnum;
import cn.hutool.extra.mail.MailAccount;
import lombok.experimental.UtilityClass;

/**
 * @author hncboy
 * @date 2023/6/25
 * 邮件工具类
 */
@UtilityClass
public class EmailUtil {

    /**
     * 获取 MailAccount
     *
     * @return MailAccount
     */
    public MailAccount getMailAccount() {
        String mailConfigStr = SysParamCache.get(SysParamKeyEnum.EMAIL_CONFIG);
        EmailConfig emailConfig = ObjectMapperUtil.fromJson(mailConfigStr, EmailConfig.class);
        MailAccount mailAccount = new MailAccount();
        mailAccount.setHost(emailConfig.getHost());
        mailAccount.setPort(emailConfig.getPort());
        mailAccount.setFrom(emailConfig.getFrom());
        mailAccount.setUser(emailConfig.getUser());
        mailAccount.setAuth(emailConfig.getAuth());
        mailAccount.setPass(emailConfig.getPass());
        mailAccount.setStarttlsEnable(emailConfig.getStartttlsEnable());
        mailAccount.setSslEnable(emailConfig.getSslEnable());
        mailAccount.setDebug(false);
        return mailAccount;
    }

    /**
     * 获取邮箱注册登录配置
     *
     * @return 邮箱注册登录配置
     */
    public EmailRegisterLoginConfig getRegisterAccountConfig() {
        String emailRegisterAccountConfigStr = SysParamCache.get(SysParamKeyEnum.EMAIL_REGISTER_LOGIN_CONFIG);
        return ObjectMapperUtil.fromJson(emailRegisterAccountConfigStr, EmailRegisterLoginConfig.class);
    }
}
