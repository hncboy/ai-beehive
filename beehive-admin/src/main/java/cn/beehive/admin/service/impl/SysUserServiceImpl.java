package cn.beehive.admin.service.impl;

import cn.beehive.admin.domain.request.SysUserLoginRequest;
import cn.beehive.admin.service.SysUserService;
import cn.beehive.base.config.ChatConfig;
import cn.beehive.base.exception.AuthException;
import cn.beehive.base.util.StpAdminUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023-3-28
 * 系统用户业务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private ChatConfig chatConfig;

    @Override
    public void login(SysUserLoginRequest sysUserLoginRequest) {
        if (sysUserLoginRequest.getAccount().equals(chatConfig.getAdminAccount()) && sysUserLoginRequest.getPassword().equals(chatConfig.getAdminPassword())) {
            StpAdminUtil.login(sysUserLoginRequest.getAccount());
            return;
        }
        throw new AuthException("账号或密码错误");
    }
}
