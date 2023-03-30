package com.hncboy.chatgpt.admin.service;

import com.hncboy.chatgpt.admin.domain.request.SysUserLoginRequest;
import com.hncboy.chatgpt.admin.domain.vo.RateLimitVO;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/28 12:43
 * 系统用户相关接口
 */
public interface SysUserService {

    /**
     * 登录
     *
     * @param sysUserLoginRequest 登录参数
     */
    void login(SysUserLoginRequest sysUserLoginRequest);

    /**
     * 查询限流列表
     *
     * @return 限流列表
     */
    List<RateLimitVO> listRateLimit();
}
