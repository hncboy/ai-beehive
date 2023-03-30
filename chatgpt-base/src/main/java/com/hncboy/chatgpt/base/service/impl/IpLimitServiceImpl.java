package com.hncboy.chatgpt.base.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.service.IpLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author lizhongyuan
 */
@Service("CommonIpLimitServiceImpl")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IpLimitServiceImpl implements IpLimitService {

    private final ChatConfig chatConfig;

    private LoadingCache<String, RateLimiter> ipCache;

    @PostConstruct
    void init() {
        ipCache = CacheBuilder.newBuilder()
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .maximumSize(100000)
                .expireAfterWrite(chatConfig.getMaxRequestSecond(), TimeUnit.SECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull RateLimiter load(String s) {
                        return RateLimiter.create(NumberUtil.div(chatConfig.getMaxRequest(), chatConfig.getMaxRequestSecond()).doubleValue());
                    }
                });
    }

    @Override
    public RateLimiter getIpLimiter(String ip) {
        try {
            return ipCache.get(ip);
        } catch (ExecutionException e) {
            log.error("获取限流器失败");
            return null;
        }
    }
}
