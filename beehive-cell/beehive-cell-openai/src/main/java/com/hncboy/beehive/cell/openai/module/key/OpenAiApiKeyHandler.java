package com.hncboy.beehive.cell.openai.module.key;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.beehive.base.domain.entity.OpenAiApiKeyDO;
import com.hncboy.beehive.base.enums.OpenAiApiKeyUseSceneEnum;
import com.hncboy.beehive.cell.openai.enums.OpenAiChatApiModelEnum;
import lombok.experimental.UtilityClass;

/**
 * @author hncboy
 * @date 2023/7/1
 * OpenAi ApiKey 相关处理
 */
@UtilityClass
public class OpenAiApiKeyHandler {

    /**
     * 获取对话的 ApiKey 信息
     *
     * @param originalApiKey         原始 ApiKey
     * @param originalBaseUrl        原始 baseUrl
     * @param apiKeyStrategyEnumCode 策略枚举 code
     * @param modeName           对话模型
     * @return ApiKey 信息
     */
    public Pair<String, String> getChatApiKeyInfo(String originalApiKey, String originalBaseUrl, String apiKeyStrategyEnumCode, String modeName) {
        // 有值就直接返回
        if (StrUtil.isNotBlank(originalApiKey)) {
            return new Pair<>(originalApiKey, originalBaseUrl);
        }

        OpenAiApiKeyUseSceneEnum useSceneEnum = OpenAiChatApiModelEnum.NAME_MAP.get(modeName).getUseSceneEnum();
        OpenAiApiKeyDO apiKeyInfo = SpringUtil.getBean(OpenAiApiKeyFactory.class).getOpenAiApiKeyStrategy(apiKeyStrategyEnumCode).getApiKeyInfo(useSceneEnum);
        return new Pair<>(apiKeyInfo.getApiKey(), apiKeyInfo.getBaseUrl());
    }

    /**
     * 获取绘图的 ApiKey 信息
     *
     * @param originalApiKey         原始 ApiKey
     * @param originalBaseUrl        原始 baseUrl
     * @param apiKeyStrategyEnumCode 策略枚举 code
     * @return ApiKey 信息
     */
    public Pair<String, String> getImageApiKeyInfo(String originalApiKey, String originalBaseUrl, String apiKeyStrategyEnumCode) {
        // 有值就直接返回
        if (StrUtil.isNotBlank(originalApiKey)) {
            return new Pair<>(originalApiKey, originalBaseUrl);
        }

        OpenAiApiKeyDO apiKeyInfo = SpringUtil.getBean(OpenAiApiKeyFactory.class).getOpenAiApiKeyStrategy(apiKeyStrategyEnumCode).getApiKeyInfo(OpenAiApiKeyUseSceneEnum.IMAGE);
        return new Pair<>(apiKeyInfo.getApiKey(), apiKeyInfo.getBaseUrl());
    }
}
