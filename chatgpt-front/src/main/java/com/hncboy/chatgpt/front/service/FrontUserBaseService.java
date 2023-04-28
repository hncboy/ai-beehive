package com.hncboy.chatgpt.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;

/**
 * 前端用户基础用户业务接口
 *
 * @author CoDeleven
 */
public interface FrontUserBaseService extends IService<FrontUserBaseDO> {

    /**
     * 创建一个空的基础用户信息
     *
     * @return 用户信息
     */
    FrontUserBaseDO createEmptyBaseUser();

    /**
     * 获取基础用户信息
     *
     * @param baseUserId 基础用户 id
     * @return 用户信息
     */
    FrontUserBaseDO findUserInfoById(Integer baseUserId);

    /**
     * 更新上次登录 ip
     *
     * @param baseUserId 基础用户 id
     */
    void updateLastLoginIp(Integer baseUserId);
}
