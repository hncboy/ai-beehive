package com.hncboy.chatgpt.base.handler;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.NumberUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.hncboy.chatgpt.base.config.ChatConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author lizhongyuan
 * ip 限流
 */
@Slf4j
public class IpRateLimiterHandler {

    private static final LoadingCache<String, RateLimiter> IP_CACHE = CacheBuilder.newBuilder()
            // 设置并发级别为 CPU 核心数
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .expireAfterAccess(ChatConfig.getMaxRequestSecond(), TimeUnit.SECONDS)
            .build(new CacheLoader<>() {
                @Override
                public @NotNull RateLimiter load(@NotNull String s) {
                    // 根据指定时间内的可访问数创建一个指定速率的 RateLimiter
                    return RateLimiter.create(NumberUtil.div(ChatConfig.getMaxRequest(), ChatConfig.getMaxRequestSecond()).doubleValue());
                }
            });

    /**
     * 根据 ip 从缓存中获取其限流器
     *
     * @param ip ip
     * @return true 允许请求
     */
    public static boolean allowRequest(String ip) {
        try {
            // 从缓存中获取对应的RateLimiter对象
            RateLimiter rateLimiter = IP_CACHE.get(ip);
            // 尝试获取令牌，如果获取成功则说明可以通过请求
            return Opt.ofNullable(rateLimiter.tryAcquire()).orElse(false);
        } catch (ExecutionException e) {
            log.error("限流器获取异常:[{}]", e.getMessage());
            return false;
        }
    }

}
