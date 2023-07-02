package com.hncboy.beehive.cell.openai.module.key;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.OpenAiApiKeyDO;
import com.hncboy.beehive.base.enums.OpenAiApiKeyStatusEnum;
import com.hncboy.beehive.base.enums.OpenAiApiKeyUseSceneEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.cell.openai.enums.OpenAiApiKeyStrategyEnum;
import com.hncboy.beehive.cell.openai.service.OpenAiApiKeyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/7/2
 * OpenAi ApiKey 余额策略
 * 选择余额最高的，余额一样按 id 降序排序
 */
@Component
public class OpenAiApiKeyBalanceStrategy implements OpenAiApiKeyStrategy {

    @Resource
    private OpenAiApiKeyService openAiApiKeyService;

    @Override
    public OpenAiApiKeyStrategyEnum getStrategyEnum() {
        return OpenAiApiKeyStrategyEnum.BALANCE;
    }

    @Override
    public OpenAiApiKeyDO getApiKeyInfo(OpenAiApiKeyUseSceneEnum useSceneEnum) {
        OpenAiApiKeyDO openAiApiKeyDO = openAiApiKeyService.getOne(new LambdaQueryWrapper<OpenAiApiKeyDO>()
                // 查询启用的
                .eq(OpenAiApiKeyDO::getStatus, OpenAiApiKeyStatusEnum.ENABLE)
                // 查询指定使用场景的
                .like(OpenAiApiKeyDO::getUseScenes, useSceneEnum.getCode())
                // 按余额降序
                .orderByDesc(OpenAiApiKeyDO::getRemainBalance)
                // 按 id 降序
                .orderByDesc(OpenAiApiKeyDO::getId)
                .last(" limit 1"));
        if (Objects.isNull(openAiApiKeyDO)) {
            throw new ServiceException("缺少 OpenAi ApiKey 配置");
        }

        return openAiApiKeyDO;
    }
}
