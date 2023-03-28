package com.hncboy.chatgpt.base.config;

import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

/**
 * @author hncboy
 * @date 2023/3/25 19:58
 * H2 Server 配置
 */
@Configuration
public class H2ServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(name = "spring.datasource.h2-server-enabled", havingValue = "true")
    public Server h2Server() throws SQLException {
        // 启动一个 Server，方便其他进程连接
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }
}
