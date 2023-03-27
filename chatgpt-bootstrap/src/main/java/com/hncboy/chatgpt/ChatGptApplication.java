package com.hncboy.chatgpt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hncboy
 * @date 2023/3/22 16:50
 * ChatGptApplication
 */
@MapperScan(value = {"com.hncboy.chatgpt.front.mapper", "com.hncboy.chatgpt.admin.mapper"})
@SpringBootApplication
public class ChatGptApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatGptApplication.class, args);
    }
}
