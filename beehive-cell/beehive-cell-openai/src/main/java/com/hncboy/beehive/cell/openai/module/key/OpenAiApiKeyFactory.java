package com.hncboy.beehive.cell.openai.module.key;

import com.hncboy.beehive.cell.openai.enums.OpenAiApiKeyStrategyEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/7/1
 * OpenAi ApiKey 工厂
 */
@Component
public class OpenAiApiKeyFactory {

    private final Map<OpenAiApiKeyStrategyEnum, OpenAiApiKeyStrategy> strategies = new HashMap<>();

    public OpenAiApiKeyFactory(List<OpenAiApiKeyStrategy> strategyList) {
        for (OpenAiApiKeyStrategy strategy : strategyList) {
            strategies.put(strategy.getStrategyEnum(), strategy);
        }
    }

    /**
     * 获取策略
     *
     * @param strategyEnum 策略枚举
     * @return 策略
     */
    public OpenAiApiKeyStrategy getOpenAiApiKeyStrategy(OpenAiApiKeyStrategyEnum strategyEnum) {
        return strategies.get(strategyEnum);
    }

    /**
     * 获取策略
     *
     * @param strategyEnumCode 策略枚举编码
     * @return 策略
     */
    public OpenAiApiKeyStrategy getOpenAiApiKeyStrategy(String strategyEnumCode) {
        return getOpenAiApiKeyStrategy(OpenAiApiKeyStrategyEnum.CODE_MAP.get(strategyEnumCode));
    }
}
