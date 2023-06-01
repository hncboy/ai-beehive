package cn.beehive.admin.service.impl;

import cn.beehive.admin.domain.request.SysUserLoginRequest;
import cn.beehive.admin.service.SysUserService;
import cn.beehive.base.util.StpAdminUtil;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023-3-28
 * 系统用户业务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Override
    public void login(SysUserLoginRequest sysUserLoginRequest) {
        // TODO: 从系统参数中获取账号密码
        StpAdminUtil.login("admin");
    }
}
