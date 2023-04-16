package com.hncboy.chatgpt.base.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hncboy
 * @date 2023/3/28 12:48
 * SaToken 配置，目前针对管理端鉴权
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        // TODO 针对 front 和 admin 分开校验
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                // 放行管理端登录接口
                .excludePathPatterns("/admin/sys_user/login")
                // 放行用户端校验邮箱验证码
                .excludePathPatterns("/user/verify_email_code")
                // 放行用户端邮箱注册
                .excludePathPatterns("/user/register/email")
                // 放行用户端图形验证码
                .excludePathPatterns("/user/get_pic_code")
                // 放行用户端邮箱登录
                .excludePathPatterns("/user/login/email");
    }

    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForStateless();
    }
}
