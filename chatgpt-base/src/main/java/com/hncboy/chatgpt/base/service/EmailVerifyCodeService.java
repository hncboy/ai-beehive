package com.hncboy.chatgpt.base.service;

import com.hncboy.chatgpt.base.domain.entity.EmailVerifyCodeDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;

/**
 * 针对表【email_verify_code(邮箱验证码核销记录表，记录某个邮箱发送了什么验证码，方便验证)】的数据库操作Service
* @author CoDeleven
*/
public interface EmailVerifyCodeService extends IService<EmailVerifyCodeDO> {

    EmailVerifyCodeDO createVerifyCode(EmailBizTypeEnum registerVerify, String identity);

    /**
     * 根据验证码查找一个有效（未过期，未验证）的验证记录
     *
     * @param verifyCode 验证码
     * @return 验证记录
     */
    EmailVerifyCodeDO findAvailableByVerifyCode(String verifyCode);

    /**
     * 完成验证记录
     *
     * @param availableVerifyCode 待完成的验证记录
     */
    void verifySuccess(EmailVerifyCodeDO availableVerifyCode);
}
