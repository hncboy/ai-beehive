package com.hncboy.chatgpt.front.service.impl;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.hncboy.chatgpt.base.config.EmailConfig;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import com.hncboy.chatgpt.front.service.EmailService;
import com.hncboy.chatgpt.front.service.SysEmailSendLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

/**
 * 邮箱注册类型策略实现类
 *
 * @author CoDeleven
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final EmailConfig emailConfig;

    private final MailAccount mailAccount;

    private final SysEmailSendLogService emailLogService;

    private final TemplateSpec templateSpec;
    private final SpringTemplateEngine templateEngine;

    public EmailServiceImpl(EmailConfig emailConfig, SysEmailSendLogService emailLogService) {
        this.templateEngine = new SpringTemplateEngine();
        // 获取邮件模板
        this.templateEngine.setTemplateResolver(new ClassLoaderTemplateResolver());
        this.templateSpec = new TemplateSpec("templates/register_verify_email.html", StandardCharsets.UTF_8.name());
        this.emailConfig = emailConfig;
        this.emailLogService = emailLogService;

        mailAccount = new MailAccount();
        mailAccount.setHost(emailConfig.getHost());
        mailAccount.setPort(Integer.parseInt(emailConfig.getPort()));
        mailAccount.setFrom(emailConfig.getFrom());
        mailAccount.setUser(emailConfig.getUser());
        mailAccount.setAuth(emailConfig.getAuth());
        mailAccount.setDebug(false);
        mailAccount.setSslEnable(true);
        mailAccount.setPass(emailConfig.getPass());
        log.info("初始化邮箱账号完毕，配置信息为：{} ", emailConfig);
    }

    @Override
    public void sendForVerifyCode(String targetEmail, String verifyCode) {
        // 设置模板中需要填充的变量
        Context context = new Context();
        context.setVariable("verificationUrl", emailConfig.getVerificationRedirectUrl().concat(verifyCode));
        // 进行渲染
        String renderedTemplate = templateEngine.process(templateSpec, context);

        // 记录日志
        try {
            String sendMsgId = this.sendMessage(targetEmail, renderedTemplate);
            emailLogService.createSuccessLogBySysLog(sendMsgId, mailAccount.getFrom(), targetEmail, EmailBizTypeEnum.REGISTER_VERIFY, renderedTemplate);
        } catch (Exception e) {
            // FIXME 发送失败前端仍然显示成功
            emailLogService.createFailedLogBySysLog("", mailAccount.getFrom(), targetEmail, EmailBizTypeEnum.REGISTER_VERIFY, renderedTemplate, e.getMessage());
        }
    }

    /**
     * 发送消息
     *
     * @param targetEmail 目标邮件地址
     * @param content     内容
     * @return 响应
     */
    protected String sendMessage(String targetEmail, String content) {
        return MailUtil.send(mailAccount, targetEmail, "【chatGPT】账号注册", content, true);
    }

    public static void main(String[] args) {
//              - EMAIL_HOST=smtp.qq.com
//      - EMAIL_PORT=465
//      - EMAIL_FROM=pigtalk@foxmail.com
//      - EMAIL_USER=pigtalk@foxmail.com
//      - EMAIL_PASS=wlzjxoevpmjjbbea
//      - EMAIL_VERIFY_REDIRECT_URL=https://pig3.pigchat.top/#/emailValidation?type=email&verifyCode=
        MailAccount
                mailAccount = new MailAccount();
        mailAccount.setHost("smtp.qq.com");
        mailAccount.setPort(465);
        mailAccount.setFrom("pigtalk@foxmail.com");
        mailAccount.setUser("pigtalk@foxmail.com");
        mailAccount.setAuth(true);
        mailAccount.setDebug(false);
        mailAccount.setSslEnable(true);
        mailAccount.setPass("wlzjxoevpmjjbbea");
        String asd = MailUtil.send(mailAccount, "lizhiheng@lensung.com", "【pig chat】账号注册", "李志恒测试", true);
        System.out.println(asd);
    }
}
