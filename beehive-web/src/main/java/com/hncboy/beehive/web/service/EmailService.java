package com.hncboy.beehive.web.service;

/**
 * 邮箱服务
 *
 * @author CoDeleven
 */
public interface EmailService {

    /**
     * 发送邮箱验证码到目标邮箱里去
     *
     * @param targetEmail 目标邮箱
     * @param verifyCode  验证码
     * @return 是否发送成功
     */
    Boolean sendForVerifyCode(String targetEmail, String verifyCode);
}
