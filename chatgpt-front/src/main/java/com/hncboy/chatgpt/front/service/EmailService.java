package com.hncboy.chatgpt.front.service;

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
     */
    void sendForVerifyCode(String targetEmail, String verifyCode);
}
