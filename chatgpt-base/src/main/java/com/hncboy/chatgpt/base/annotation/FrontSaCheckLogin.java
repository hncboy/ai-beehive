package com.hncboy.chatgpt.base.annotation;

import cn.dev33.satoken.annotation.SaCheckLogin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 前端用户登录校验注解
 * @author CoDeleven
 */
@SaCheckLogin(type = "front")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface FrontSaCheckLogin {
}
