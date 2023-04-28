package com.hncboy.chatgpt.admin.controller;

import com.hncboy.chatgpt.admin.domain.request.SysUserLoginRequest;
import com.hncboy.chatgpt.admin.service.SysUserService;
import com.hncboy.chatgpt.base.annotation.ApiAdminRestController;
import com.hncboy.chatgpt.base.handler.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author hncboy
 * @date 2023-3-28
 * 系统用户相关接口
 */
@AllArgsConstructor
@Tag(name = "管理端-系统用户相关接口")
@ApiAdminRestController("/sys_user")
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<Void> login(@Validated @RequestBody SysUserLoginRequest sysUserLoginRequest) {
        sysUserService.login(sysUserLoginRequest);
        return R.success("登录成功");
    }
}
