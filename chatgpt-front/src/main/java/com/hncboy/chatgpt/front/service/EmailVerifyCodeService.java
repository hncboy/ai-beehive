package com.hncboy.chatgpt.front.service;

import com.hncboy.chatgpt.base.domain.entity.EmailVerifyCodeDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;

/**
 * 邮箱验证码核销记录业务接口
 *
 * @author CoDeleven
 */
public interface EmailVerifyCodeService extends IService<EmailVerifyCodeDO> {

    /**
     * 创建校验码
     *
     * @param emailBizTypeEnum 业务用途枚举
     * @param identity         标识
     * @return 校验码
     */
    EmailVerifyCodeDO createVerifyCode(EmailBizTypeEnum emailBizTypeEnum, String identity);

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
