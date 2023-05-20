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

    private static final StringRedisTemplate STRING_REDIS_TEMPLATE;

    static {
        STRING_REDIS_TEMPLATE = SpringUtil.getBean(StringRedisTemplate.class);
    }

    /**
     * 设置指定 key 的值并不过期
     *
     * @param key   key
     * @param value value
     */
    public static void set(String key, String value) {
        STRING_REDIS_TEMPLATE.opsForValue().set(key, value);
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
        STRING_REDIS_TEMPLATE.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * key 不存在的情况设置 key并不过期
     *
     * @param key   key
     * @param value value
     * @return set 是否成功
     */
    public static Boolean setIfAbsent(String key, String value) {
        return STRING_REDIS_TEMPLATE.opsForValue().setIfAbsent(key, value);
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
        return STRING_REDIS_TEMPLATE.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 删除 key
     *
     * @param key key
     * @return 是否删除成功
     */
    public static Boolean delete(String key) {
        return STRING_REDIS_TEMPLATE.delete(key);
    }

    /**
     * 获取指定 key 的值
     *
     * @param key key
     * @return value
     */
    public static String get(String key) {
        return STRING_REDIS_TEMPLATE.opsForValue().get(key);
    }

    /**
     * 是否存在 key
     *
     * @param key key
     * @return true/false
     */
    public static Boolean hasKey(String key) {
        return STRING_REDIS_TEMPLATE.hasKey(key);
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
        return STRING_REDIS_TEMPLATE.expire(key, timeout, unit);
    }

    /** ------------------------list相关操作---------------------------- */

    /**
     * 存储在 list 头部
     *
     * @param key   key
     * @param value value
     * @return list 长度
     */
    public static Long lLeftPush(String key, String value) {
        return STRING_REDIS_TEMPLATE.opsForList().leftPush(key, value);
    }

    /**
     * 移出并获取列表的第一个元素
     *
     * @param key key
     * @return 删除的元素
     */
    public static String lLeftPop(String key) {
        return STRING_REDIS_TEMPLATE.opsForList().leftPop(key);
    }

    /**
     * 获取列表长度
     *
     * @param key key
     * @return 长度
     */
    public static Long lLen(String key) {
        return STRING_REDIS_TEMPLATE.opsForList().size(key);
    }
}

