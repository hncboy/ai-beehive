package com.hncboy.beehive.base.constant;

/**
 * @author hncboy
 * @date 2023/5/11
 * 公共常量
 */
public class PublicConstant {

    /**
     * 防止 Redis 缓存穿透的 value
     */
    public static final String REDIS_CACHE_MISS_VALUE = "ForbidCacheMiss";
}
