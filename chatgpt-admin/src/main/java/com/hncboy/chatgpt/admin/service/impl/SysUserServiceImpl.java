package com.hncboy.chatgpt.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.hncboy.chatgpt.admin.domain.request.SysUserLoginRequest;
import com.hncboy.chatgpt.admin.service.SysUserService;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.exception.AuthException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author hncboy
 * @date 2023/3/28 12:44
 * 系统用户业务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private ChatConfig chatConfig;

    @Override
    public void login(SysUserLoginRequest sysUserLoginRequest) {
        if (sysUserLoginRequest.getAccount().equals(chatConfig.getAdminAccount()) && sysUserLoginRequest.getPassword().equals(chatConfig.getAdminPassword())) {
            StpUtil.login(sysUserLoginRequest.getAccount());
            return;
        }
        throw new AuthException("账号或密码错误");
    }
}
