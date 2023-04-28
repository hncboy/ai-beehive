package com.hncboy.chatgpt.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraBindingDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;

/**
 * 前端用户绑定相关业务接口
 *
 * @author CoDeleven
 */
public interface FrontUserExtraBindingService extends IService<FrontUserExtraBindingDO> {

    /**
     * 绑定邮箱
     *
     * @param baseUser     基础用户
     * @param extraEmailDO 邮件扩展信息
     */
    void bindEmail(FrontUserBaseDO baseUser, FrontUserExtraEmailDO extraEmailDO);

    /**
     * 找到绑定关系
     *
     * @param frontUserRegisterTypeEnum 注册类型
     * @param extraInfoId               扩展信息 id
     * @return 绑定关系
     */
    FrontUserExtraBindingDO findExtraBinding(FrontUserRegisterTypeEnum frontUserRegisterTypeEnum, Integer extraInfoId);
}
