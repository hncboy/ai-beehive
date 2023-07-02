package com.hncboy.beehive.web.service.impl;

import com.hncboy.beehive.web.domain.request.SysUserLoginRequest;
import com.hncboy.beehive.web.service.SysUserService;
import com.hncboy.beehive.base.cache.SysParamCache;
import com.hncboy.beehive.base.exception.AuthException;
import com.hncboy.beehive.base.util.StpAdminUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023-3-28
 * 系统用户业务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    private static final String ADMIN_ACCOUNT = "admin-account";
    private static final String ADMIN_PASSWORD = "admin-password";

    @Override
    public void login(SysUserLoginRequest sysUserLoginRequest) {
        Map<String, String> adminInfoMap = SysParamCache.multiGet(Arrays.asList(ADMIN_ACCOUNT, ADMIN_PASSWORD));
        if (sysUserLoginRequest.getAccount().equals(adminInfoMap.get(ADMIN_ACCOUNT)) && sysUserLoginRequest.getPassword().equals(adminInfoMap.get(ADMIN_PASSWORD))) {
            StpAdminUtil.login(sysUserLoginRequest.getAccount());
            return;
        }
        throw new AuthException("账号或密码错误");
    }
}
