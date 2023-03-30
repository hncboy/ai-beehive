package com.hncboy.chatgpt.base.handler;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.hncboy.chatgpt.base.config.ChatConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author lizhongyuan
 * ip 限流
 */
@Slf4j
public class IpRateLimiterHandler {

    private static final Map<String, RateLimiter> LIMITER_MAP = Maps.newConcurrentMap();

    /**
     * 根据ip请求是否被限制请求
     *
     * @param ip ip
     * @return true 允许请求
     */
    public static boolean allowRequest(String ip) {
        ChatConfig chatConfig = SpringUtil.getBean(ChatConfig.class);
        // 从 Map 中获取对应的RateLimiter对象
        return LIMITER_MAP.computeIfAbsent(
                ip,
                key -> RateLimiter.create(NumberUtil.div(chatConfig.getMaxRequest(), chatConfig.getMaxRequestSecond()).doubleValue())
        ).tryAcquire();
    }

}
