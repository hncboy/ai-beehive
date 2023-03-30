package com.hncboy.chatgpt.base.service;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @author lizhongyuan
 */
public interface IpLimitService {

    /**
     * 根据ip获取限流器
     * @param ip ip
     * @return 限流去
     */
    RateLimiter getIpLimiter(String ip);
}
