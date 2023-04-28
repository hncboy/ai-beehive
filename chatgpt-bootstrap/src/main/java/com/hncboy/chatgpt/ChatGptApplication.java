package com.hncboy.chatgpt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

/**
 * @author hncboy
 * @date 2023-3-22
 * ChatGptApplication
 */
@MapperScan(value = {"com.hncboy.**.mapper"})
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class ChatGptApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatGptApplication.class, args);
    }
}
