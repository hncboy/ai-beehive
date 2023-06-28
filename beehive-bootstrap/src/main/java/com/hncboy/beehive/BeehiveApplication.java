package com.hncboy.beehive;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author hncboy
 * @date 2023-3-22
 * BeehiveApplication
 */
@EnableScheduling
@MapperScan(value = {"com.hncboy.beehive.**.mapper"})
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class BeehiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeehiveApplication.class, args);
    }
}
