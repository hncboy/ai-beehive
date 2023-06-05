package cn.beehive.web.service.impl;

import cn.beehive.base.cache.SysParamCache;
import cn.beehive.base.enums.EmailBizTypeEnum;
import cn.beehive.base.resource.email.EmailConfig;
import cn.beehive.web.service.EmailService;
import cn.beehive.web.service.SysEmailSendLogService;
import cn.hutool.extra.mail.MailUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * 邮箱注册类型策略实现类
 *
 * @author CoDeleven
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    /**
     * 邮箱注册模板内容 sysParamKey
     */
    private static final String REGISTER_EMAIL_TEMPLATE_CONTENT_SYS_PARAM_KEY = "email-registerTemplateContent";

    /**
     * 邮箱注册模板主题 sysParamKey
     */
    private static final String REGISTER_EMAIL_TEMPLATE_SUBJECT_SYS_PARAM_KEY = "email-registerTemplateSubject";

    @Resource
    private EmailConfig emailConfig;

    @Resource
    private SysEmailSendLogService emailLogService;

    @Override
    public void sendForVerifyCode(String targetEmail, String verifyCode) {
        // 记录日志
        String sendContent = getSendContent(verifyCode);
        try {
            String sendMsgId = sendMessage(targetEmail, sendContent);
            emailLogService.createSuccessLogBySysLog(sendMsgId, emailConfig.getMailAccount().getFrom(), targetEmail, EmailBizTypeEnum.REGISTER_VERIFY, sendContent);
        } catch (Exception e) {
            // FIXME 发送失败前端仍然显示成功
            emailLogService.createFailedLogBySysLog("", emailConfig.getMailAccount().getFrom(), targetEmail, EmailBizTypeEnum.REGISTER_VERIFY, sendContent, e.getMessage());
        }
    }

    /**
     * 发送消息
     *
     * @param targetEmail 目标邮件地址
     * @param content     内容
     * @return 响应
     */
    private String sendMessage(String targetEmail, String content) {
        return MailUtil.send(emailConfig.getMailAccount(), targetEmail, SysParamCache.get(REGISTER_EMAIL_TEMPLATE_SUBJECT_SYS_PARAM_KEY), content, true);
    }

    /**
     * 获取发送内容
     *
     * @param verifyCode 验证码
     * @return 发送内容
     */
    private String getSendContent(String verifyCode) {
        // 设置模板中需要填充的变量
        Context context = new Context();
        context.setVariable("verificationUrl", emailConfig.getVerificationRedirectUrl().concat(verifyCode));
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();

        // 获取邮件模板
        String htmlTemplate = SysParamCache.get(REGISTER_EMAIL_TEMPLATE_CONTENT_SYS_PARAM_KEY);
        // 设置模板模式
        TemplateSpec templateSpec = new TemplateSpec(htmlTemplate, TemplateMode.HTML);
        // 进行渲染
        return templateEngine.process(templateSpec, context);
    }
}
