package com.hncboy.chatgpt.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;

/**
 * 使用邮箱登录的前端用户服务
 *
 * @author CoDeleven
 */
public interface FrontUserExtraEmailService extends IService<FrontUserExtraEmailDO> {

    /**
     * 是否已使用
     *
     * @param username 用户名，邮箱方式下是邮箱，手机方式下是手机号码
     * @return 是否已使用，true已使用；false未使用
     */
    boolean isUsed(String username);

    FrontUserExtraEmailDO getUnverifiedEmailAccount(String identity);

    FrontUserExtraEmailDO getEmailAccount(String username);
}
