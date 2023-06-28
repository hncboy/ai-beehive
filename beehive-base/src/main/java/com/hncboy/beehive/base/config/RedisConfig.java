package com.hncboy.beehive.base.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author hncboy
 * @date 2023/5/11
 * Redis 配置
 */
@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // key 序列化，使用自定义序列化方式
        redisTemplate.setKeySerializer(new RedisKeySerializer());
        // Hash key 序列化，使用自定义序列化方式
        redisTemplate.setHashKeySerializer(new RedisKeySerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * Redis Key 自定义序列化
     * 组装通用的 key 前缀
     */
    public static class RedisKeySerializer implements RedisSerializer<String> {

        /**
         * 编码格式
         */
        private final Charset charset;

        /**
         * 前缀
         */
        private final String PREFIX_KEY = "bh:";

        public RedisKeySerializer() {
            this(StandardCharsets.UTF_8);
        }

        public RedisKeySerializer(Charset charset) {
            this.charset = charset;
        }

        /**
         * 反序列化
         */
        @Override
        public String deserialize(byte[] bytes) {
            String key = new String(bytes, charset);
            return key.replace(PREFIX_KEY, StrUtil.EMPTY);
        }

        /**
         * 序列化
         */
        @Override
        public byte[] serialize(String key) {
            key = PREFIX_KEY + key;
            return key.getBytes(charset);
        }
    }
}
