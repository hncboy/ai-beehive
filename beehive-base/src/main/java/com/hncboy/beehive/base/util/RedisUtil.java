package com.hncboy.beehive.base.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    /** ------------------------ List 相关操作---------------------------- */

    /**
     * 获取列表指定范围内的元素
     *
     * @param key   key
     * @param start 开始位置, 0 是开始位置
     * @param end   结束位置, -1 返回所有
     * @return 列表
     */
    public static List<String> lRange(String key, long start, long end) {
        return STRING_REDIS_TEMPLATE.opsForList().range(key, start, end);
    }

    /**
     * 存储在 list 尾部
     *
     * @param key   key
     * @param value value
     * @return list 长度
     */
    public static Long lRightPush(String key, String value) {
        return STRING_REDIS_TEMPLATE.opsForList().rightPush(key, value);
    }

    /**
     * 从头部移出并获取列表的一个元素
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

    /** ------------------------ Hash 相关操作---------------------------- */

    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @param key   key
     * @param field field
     * @return result
     */
    public static Object hGet(String key, String field) {
        return STRING_REDIS_TEMPLATE.opsForHash().get(key, field);
    }

    /**
     * 批量获取 hashKey 对应的 value
     *
     * @param key      key
     * @param hashKeys hashKeys
     * @return List
     */
    public static List<Object> hMultiGet(String key, Collection<Object> hashKeys) {
        return STRING_REDIS_TEMPLATE.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * 获取对应 key 的所有键值对
     *
     * @param key key
     * @return value
     */
    public static Map<Object, Object> hEntries(String key) {
        return STRING_REDIS_TEMPLATE.opsForHash().entries(key);
    }

    /**
     * 插入指定的 hashKey 和 hashValue
     *
     * @param key       key
     * @param hashKey   hashKey
     * @param hashValue hashValue
     */
    public static void hPut(String key, String hashKey, String hashValue) {
        STRING_REDIS_TEMPLATE.opsForHash().put(key, hashKey, hashValue);
    }

    /**
     * 批处理插入指定的 hashKey 和 hashValue
     *
     * @param key     key
     * @param hashMap hashMap
     */
    public static void hPut(String key, Map<String, String> hashMap) {
        STRING_REDIS_TEMPLATE.opsForHash().putAll(key, hashMap);
    }

    /**
     * 仅当 hashKey 不存在时才设置
     *
     * @param key     key
     * @param hashKey hashKey
     * @param value   value
     * @return result
     */
    public static Boolean hPutIfAbsent(String key, String hashKey, String value) {
        return STRING_REDIS_TEMPLATE.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key      key
     * @param hashKeys hashKeys
     * @return result
     */
    public static Long hDelete(String key, Object... hashKeys) {
        return STRING_REDIS_TEMPLATE.opsForHash().delete(key, hashKeys);
    }
}

