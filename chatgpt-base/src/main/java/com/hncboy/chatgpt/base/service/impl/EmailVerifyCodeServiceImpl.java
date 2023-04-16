package com.hncboy.chatgpt.base.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.config.EmailConfig;
import com.hncboy.chatgpt.base.domain.entity.EmailVerifyCodeDO;
import com.hncboy.chatgpt.base.service.EmailVerifyCodeService;
import com.hncboy.chatgpt.base.mapper.EmailVerifyCodeMapper;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import com.hncboy.chatgpt.base.util.WebUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 针对表【email_verify_code(邮箱验证码核销记录表，记录某个邮箱发送了什么验证码，方便验证)】的数据库操作Service实现
* @author CoDeleven
*/
@Service
public class EmailVerifyCodeServiceImpl extends ServiceImpl<EmailVerifyCodeMapper, EmailVerifyCodeDO>
    implements EmailVerifyCodeService{

    @Resource
    private EmailConfig emailConfig;

    @Override
    public EmailVerifyCodeDO createVerifyCode(EmailBizTypeEnum bizType, String identity) {
        EmailVerifyCodeDO verifyCode = new EmailVerifyCodeDO();
        verifyCode.setVerifyCode(RandomUtil.randomString(32));
        verifyCode.setStatus(0);
        verifyCode.setVerifyIp(WebUtil.getIp());
        verifyCode.setBizType(bizType);
        verifyCode.setToEmailAddress(identity);
        verifyCode.setExpireAt(DateUtil.offsetMinute(new Date(), emailConfig.getVerifyCodeExpireMinutes()));
        this.save(verifyCode);
        return verifyCode;
    }

    @Override
    public EmailVerifyCodeDO findAvailableByVerifyCode(String verifyCode) {
        return this.getOne(new QueryWrapper<EmailVerifyCodeDO>().eq("verify_code", verifyCode)
                .eq("status", 0).gt("expire_at", new Date()));
    }

    @Override
    public void verifySuccess(EmailVerifyCodeDO availableVerifyCode) {
        availableVerifyCode.setVerifyIp(WebUtil.getIp());
        availableVerifyCode.setStatus(1);
        this.updateById(availableVerifyCode);
    }
}




