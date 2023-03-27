package com.hncboy.chatgpt.base.handler.aspect;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.exception.AuthException;
import com.hncboy.chatgpt.base.exception.ServiceException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author hncboy
 * @date 2023/3/23 00:19
 * 填写注释
 */
@Aspect
@Component
public class PreAuthAspect {

    @Resource
    private ChatConfig chatConfig;

    @Pointcut("@annotation(com.hncboy.chatgpt.base.annotation.PreAuth) || @within(com.hncboy.chatgpt.base.annotation.PreAuth)")
    public void pointcut() {

    }

    /**
     * 切 方法 和 类上的 @PreAuth 注解
     *
     * @param point 切点
     * @return Object
     * @throws Throwable 没有权限的异常
     */
    @Around("pointcut()")
    public Object checkAuth(ProceedingJoinPoint point) throws Throwable {
        // 没有设置鉴权
        if (BooleanUtil.isFalse(chatConfig.hasAuth())) {
            return point.proceed();
        }

        RequestAttributes requestAttributes = Optional.ofNullable(RequestContextHolder.getRequestAttributes()).orElseThrow(() -> new ServiceException("request is null"));
        String authorization = ServletUtil.getHeader(((ServletRequestAttributes) requestAttributes).getRequest(), "Authorization", StandardCharsets.UTF_8);
        if (StrUtil.isBlank(authorization) || !authorization.replace("Bearer ", "").trim().equals(chatConfig.getAuthSecretKey().trim())) {
            throw new AuthException("Error: 无访问权限 | No access rights");
        }

        return point.proceed();
    }
}
