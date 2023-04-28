package com.hncboy.chatgpt.base.handler.runner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hncboy.chatgpt.base.handler.RateLimiterHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileReader;

import static com.hncboy.chatgpt.base.handler.RateLimiterHandler.GLOBAL_REQUEST_TIMESTAMP_QUEUE;
import static com.hncboy.chatgpt.base.handler.RateLimiterHandler.IP_REQUEST_TIMESTAMP_MAP;

/**
 * @author hncboy
 * @date 2023-4-1
 * 限流器启动器
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class RateLimiterRunner implements ApplicationRunner {

    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) {
        try {
            File file = new File(RateLimiterHandler.FILE_PATH);

            // 如果文件不存在，创建它
            if (!file.exists()) {
                if (!file.getParentFile().mkdirs()) {
                    log.warn("创建限流文件父级目录失败...");
                }
                if (!file.createNewFile()) {
                    log.warn("创建限流文件失败...");
                }
            }
            if (file.length() <= 0) {
                return;
            }
            // 文件长度大于 0 才读取
            try (FileReader fr = new FileReader(file)) {
                RateLimiterHandler.TimestampPair timestampPair = objectMapper.readValue(fr, new TypeReference<>() {
                });
                GLOBAL_REQUEST_TIMESTAMP_QUEUE.addAll(timestampPair.getGlobalQueue());
                IP_REQUEST_TIMESTAMP_MAP.putAll(timestampPair.getIpMap());
            }
        } catch (Exception e) {
            log.error("限流器启动器启动失败", e);
        }
    }
}
