package com.hncboy.chatgpt.base.handler.runner;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.chatgpt.base.domain.entity.SensitiveWordDO;
import com.hncboy.chatgpt.base.enums.EnableDisableStatusEnum;
import com.hncboy.chatgpt.base.service.SensitiveWordService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/3/28 22:22
 * 敏感词启动器
 */
@Configuration
@AllArgsConstructor
public class SensitiveWordRunner implements ApplicationRunner {

    private final SensitiveWordService sensitiveWordService;

    private final ResourceLoader resourceLoader;

    @Override
    public void run(ApplicationArguments args) {
        try {
            // 从本地文件读取敏感词
            Resource resource = resourceLoader.getResource("classpath:data/sensitive_word_base64.txt");
            List<String> sensitiveWords = new ArrayList<>();
            IoUtil.readLines(resource.getInputStream(), Charset.defaultCharset(), sensitiveWords);
            // 拆分敏感词列表
            List<List<String>> splitSensitiveWords = ListUtil.split(sensitiveWords, 100);
            for (List<String> subSensitiveWords : splitSensitiveWords) {
                // Base64 解密
                subSensitiveWords = subSensitiveWords.stream()
                        .map(Base64::decode)
                        .map(String::new)
                        .collect(Collectors.toList());

                // 查询已经存在的敏感词列表
                List<SensitiveWordDO> existSensitiveWords = sensitiveWordService.list(new LambdaQueryWrapper<SensitiveWordDO>()
                        .in(SensitiveWordDO::getWord, subSensitiveWords));

                // 已经存在的单词
                List<String> existWords = existSensitiveWords.stream().map(SensitiveWordDO::getWord).toList();
                // 构建不存在的敏感词列表
                List<SensitiveWordDO> notExistSensitiveWords = subSensitiveWords.stream()
                        .filter(word -> !existWords.contains(word))
                        .map(word -> {
                            SensitiveWordDO sensitiveWordDO = new SensitiveWordDO();
                            sensitiveWordDO.setWord(word);
                            sensitiveWordDO.setStatus(EnableDisableStatusEnum.ENABLE);
                            return sensitiveWordDO;
                        }).collect(Collectors.toList());
                // 批量插入
                sensitiveWordService.saveBatch(notExistSensitiveWords);
            }
            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
