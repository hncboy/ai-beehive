package com.hncboy.chatgpt.base.util;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.exception.ServiceException;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author hncboy
 * @date 2023-3-27
 * Web 相关工具类
 */
@UtilityClass
public class WebUtil {


    /**
     * IP 头部名称数组
     * <p>
     * x-forwarded-for：代理服务器转发的客户端 IP
     * X-Real-IP：某些代理服务器使用的真实客户端 IP
     * CF-Connecting-IP：Cloudflare 代理服务器提供的客户端 IP
     * Proxy-Client-IP：HTTP 代理或负载均衡服务器 IP
     * WL-Proxy-Client-IP：WebLogic 代理的客户端 IP
     * HTTP_CLIENT_IP：有些代理服务器会加上此请求头部
     * HTTP_X_FORWARDED_FOR：有些代理服务器会加上此请求头部
     */
    private static final String[] IP_HEADER_NAMES = new String[]{
            "x-forwarded-for",
            "X-Real-IP",
            "CF-Connecting-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    /**
     * IP 地址的断言条件
     */
    private static final Predicate<String> IP_PREDICATE = (ip) -> StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip);

    /**
     * 获取当前请求的 HttpServletRequest 对象
     *
     * @return {HttpServletRequest}
     */
    public HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = Optional.ofNullable(RequestContextHolder.getRequestAttributes()).orElseThrow(() -> new ServiceException("request is null"));
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 获取当前请求的 IP 地址
     *
     * @return {String}
     */
    public static String getIp() {
        return getIp(WebUtil.getRequest());
    }

    /**
     * 获取请求的 IP 地址
     *
     * @param request HttpServletRequest 对象
     * @return {String}
     */
    @Nullable
    public static String getIp(@Nullable HttpServletRequest request) {
        if (request == null) {
            return StrUtil.EMPTY;
        }
        String ip = null;
        // 遍历 IP 头部名称数组
        for (String ipHeader : IP_HEADER_NAMES) {
            // 从 HttpServletRequest 中获取 IP 头部的值
            ip = request.getHeader(ipHeader);
            if (!IP_PREDICATE.test(ip)) {
                // 如果 IP 地址非空，即找到了有效的 IP 地址，则跳出循环
                break;
            }
        }
        if (IP_PREDICATE.test(ip)) {
            // 如果遍历 IP 头部名称数组后仍未找到有效的 IP 地址，则从 RemoteAddr 中获取 IP 地址
            ip = request.getRemoteAddr();
        }
        // 返回第一个逗号分隔的 IP 地址，多个 IP 地址时取第一个 IP 地址
        return StrUtil.isBlank(ip) ? null : StrUtil.splitTrim(ip, StrPool.COMMA).get(0);
    }
}
