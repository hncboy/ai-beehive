package com.hncboy.chatgpt.base.config;

import com.hncboy.chatgpt.base.handler.serializer.DateFormatterSerializer;
import com.hncboy.chatgpt.base.handler.serializer.LongToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023-4-1
 * Jackson 配置
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        // 返回 Long 转 String
        builder.serializerByType(Long.class, new LongToStringSerializer());
        // 返回 Date 格式化
        builder.serializerByType(Date.class, new DateFormatterSerializer());
        return builder;
    }
}