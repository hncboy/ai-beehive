package cn.beehive.base.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author hncboy
 * @date 2023/5/10
 * Redis 工具类
 */
public class RedisUtil {

    private static final StringRedisTemplate stringRedisTemplate;

    static {
        stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
    }

    /**
     * 设置指定 key 的值并不过期
     *
     * @param key   key
     * @param value value
     */
    public static void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置指定 key 的值并指定过期时间
     *
     * @param key     key
     * @param value   value
     * @param timeout 过期时间
     * @param unit    单位
     */
    public static void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * key 不存在的情况设置 key并不过期
     *
     * @param key   key
     * @param value value
     * @return set 是否成功
     */
    public static Boolean setIfAbsent(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * key 不存在的情况设置 key，指定过期时间
     *
     * @param key     key
     * @param value   value
     * @param timeout 超时时长
     * @param unit    timeout 的单位
     * @return set 是否成功
     */
    public static Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 删除 key
     *
     * @param key key
     */
    public static void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取指定 key 的值
     *
     * @param key key
     * @return value
     */
    public static String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 是否存在 key
     *
     * @param key key
     * @return true/false
     */
    public static Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key     key
     * @param timeout timeout
     * @param unit    unit
     * @return result
     */
    public static Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }
}

