package com.hncboy.chatgpt.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;

/**
 * 前端用户基础用户，后续的服务都使用这张表的ID
 *
 * @author CoDeleven
 */
public interface FrontUserBaseService extends IService<FrontUserBaseDO> {

    /**
     * 创建一个空的基础用户信息
     */
    FrontUserBaseDO createEmptyBaseUser();

    FrontUserBaseDO findUserInfoById(Integer baseUserId);
}
