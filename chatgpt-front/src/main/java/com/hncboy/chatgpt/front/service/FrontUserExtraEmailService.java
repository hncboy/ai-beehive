package com.hncboy.chatgpt.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;

/**
 * 前端用户邮箱扩展业务接口
 *
 * @author CoDeleven
 */
public interface FrontUserExtraEmailService extends IService<FrontUserExtraEmailDO> {

    /**
     * 是否已使用
     *
     * @param username 邮箱
     * @return 是否已使用，true已使用；false未使用
     */
    boolean isUsed(String username);

    /**
     * 获取一个未被验证的邮箱账号信息
     *
     * @param identity 邮箱
     * @return 邮箱信息
     */
    FrontUserExtraEmailDO getUnverifiedEmailAccount(String identity);

    /**
     * 根据用户名获取邮箱账号信息
     *
     * @param username 邮箱
     * @return 邮箱信息
     */
    FrontUserExtraEmailDO getEmailAccount(String username);

    /**
     * 邮件验证完毕
     *
     * @param emailExtraInfo 邮箱登录信息
     */
    void verifySuccess(FrontUserExtraEmailDO emailExtraInfo);
}
