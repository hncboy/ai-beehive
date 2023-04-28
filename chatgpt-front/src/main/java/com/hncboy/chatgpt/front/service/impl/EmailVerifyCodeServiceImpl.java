package com.hncboy.chatgpt.front.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.config.EmailConfig;
import com.hncboy.chatgpt.base.domain.entity.EmailVerifyCodeDO;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import com.hncboy.chatgpt.base.mapper.EmailVerifyCodeMapper;
import com.hncboy.chatgpt.base.util.ThrowExceptionUtil;
import com.hncboy.chatgpt.base.util.WebUtil;
import com.hncboy.chatgpt.front.service.EmailVerifyCodeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 邮箱验证码核销记录业务实现类
 *
 * @author CoDeleven
 */
@Service
public class EmailVerifyCodeServiceImpl extends ServiceImpl<EmailVerifyCodeMapper, EmailVerifyCodeDO> implements EmailVerifyCodeService {

    @Resource
    private EmailConfig emailConfig;

    @Override
    public EmailVerifyCodeDO createVerifyCode(EmailBizTypeEnum emailBizTypeEnum, String identity) {
        EmailVerifyCodeDO verifyCode = new EmailVerifyCodeDO();
        verifyCode.setVerifyCode(RandomUtil.randomString(32));
        verifyCode.setIsUsed(false);
        verifyCode.setVerifyIp(WebUtil.getIp());
        verifyCode.setBizType(emailBizTypeEnum);
        verifyCode.setToEmailAddress(identity);
        verifyCode.setExpireAt(DateUtil.offsetMinute(new Date(), emailConfig.getVerifyCodeExpireMinutes()));
        this.save(verifyCode);
        return verifyCode;
    }

    @Override
    public EmailVerifyCodeDO findAvailableByVerifyCode(String verifyCode) {
        return getOne(new LambdaQueryWrapper<EmailVerifyCodeDO>()
                .eq(EmailVerifyCodeDO::getVerifyCode, verifyCode)
                .eq(EmailVerifyCodeDO::getIsUsed, 0)
                // 当前时间小于过期时间
                .gt(EmailVerifyCodeDO::getExpireAt, new Date()));
    }

    @Override
    public void verifySuccess(EmailVerifyCodeDO availableVerifyCode) {
        ThrowExceptionUtil.isFalse(update(new EmailVerifyCodeDO(), new LambdaUpdateWrapper<EmailVerifyCodeDO>()
                        .set(EmailVerifyCodeDO::getVerifyIp, WebUtil.getIp())
                        .set(EmailVerifyCodeDO::getIsUsed, true)
                        // 乐观锁
                        .eq(EmailVerifyCodeDO::getIsUsed, false)
                        .eq(EmailVerifyCodeDO::getId, availableVerifyCode.getId())))
                .throwMessage("邮箱验证码核销失败");
    }
}




