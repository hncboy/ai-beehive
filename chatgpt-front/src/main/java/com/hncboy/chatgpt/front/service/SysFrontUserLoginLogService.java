package com.hncboy.chatgpt.front.service;

import com.hncboy.chatgpt.base.domain.entity.SysFrontUserLoginLogDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;

/**
 * 前端用户登录日志业务接口
 *
 * @author CoDeleven
 */
public interface SysFrontUserLoginLogService extends IService<SysFrontUserLoginLogDO> {

    /**
     * 登录失败记录表
     *
     * @param registerType 注册类型
     * @param extraInfoId  登录信息表ID
     * @param baseUserId   基础用户ID
     * @param message      失败原因
     */
    void loginFailed(FrontUserRegisterTypeEnum registerType, Integer extraInfoId, Integer baseUserId, String message);

    /**
     * 登录成功记录表
     *
     * @param registerType 注册类型
     * @param extraInfoId  登录信息表ID
     * @param baseUserId   基础用户ID
     */
    void loginSuccess(FrontUserRegisterTypeEnum registerType, Integer extraInfoId, Integer baseUserId);
}
