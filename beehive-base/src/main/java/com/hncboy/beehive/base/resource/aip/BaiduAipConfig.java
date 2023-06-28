package com.hncboy.beehive.base.resource.aip;

import com.baidu.aip.contentcensor.AipContentCensor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hncboy
 * @date 2023/6/26
 * 百度 AI 配置
 */
@Slf4j
@Configuration
public class BaiduAipConfig {

    @Bean
    public AipContentCensor aipContentCensor() {
        BaiduAipProperties baiduAipProperties = BaiduAipUtil.getBaiduAipProperties();
        log.info("百度 AI 配置初始化：{}", baiduAipProperties);
        // 项目启动时初始化，如果要修改配置，需要重启项目
        return new AipContentCensor(baiduAipProperties.getAppId(), baiduAipProperties.getAppKey(), baiduAipProperties.getSecretKey());
    }
}
