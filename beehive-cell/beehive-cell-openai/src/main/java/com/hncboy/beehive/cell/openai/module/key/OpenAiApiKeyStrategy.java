package com.hncboy.beehive.cell.openai.module.key;

import com.hncboy.beehive.base.domain.entity.OpenAiApiKeyDO;
import com.hncboy.beehive.base.enums.OpenAiApiKeyUseSceneEnum;
import com.hncboy.beehive.cell.openai.enums.OpenAiApiKeyStrategyEnum;

/**
 * @author hncboy
 * @date 2023/7/1
 * OpenAi ApiKey 策略接口
 */
public interface OpenAiApiKeyStrategy {

    /**
     * 获取策略枚举
     *
     * @return 策略枚举
     */
    OpenAiApiKeyStrategyEnum getStrategyEnum();

    /**
     * 获取 ApiKey 信息
     *
     * @param useSceneEnum 使用场景
     * @return ApiKey 配置信息
     */
    OpenAiApiKeyDO getApiKeyInfo(OpenAiApiKeyUseSceneEnum useSceneEnum);
}
