package com.hncboy.beehive.cell.openai.module.key;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.OpenAiApiKeyDO;
import com.hncboy.beehive.base.enums.OpenAiApiKeyStatusEnum;
import com.hncboy.beehive.base.enums.OpenAiApiKeyUseSceneEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.cell.openai.enums.OpenAiApiKeyStrategyEnum;
import com.hncboy.beehive.cell.openai.service.OpenAiApiKeyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/7/1
 * OpenAi ApiKey 随机策略
 */
@Component
public class OpenAiApiKeyRandomStrategy implements OpenAiApiKeyStrategy {

    @Resource
    private OpenAiApiKeyService openAiApiKeyService;

    @Override
    public OpenAiApiKeyStrategyEnum getStrategyEnum() {
        return OpenAiApiKeyStrategyEnum.RANDOM;
    }

    @Override
    public OpenAiApiKeyDO getApiKeyInfo(OpenAiApiKeyUseSceneEnum useSceneEnum) {
        List<OpenAiApiKeyDO> openAiApiKeyDOList = openAiApiKeyService.list(new LambdaQueryWrapper<OpenAiApiKeyDO>()
                // 查询启用的
                .eq(OpenAiApiKeyDO::getStatus, OpenAiApiKeyStatusEnum.ENABLE)
                // 查询指定使用场景的
                .like(OpenAiApiKeyDO::getUseScenes, useSceneEnum.getCode()));
        if (CollectionUtil.isEmpty(openAiApiKeyDOList)) {
            throw new ServiceException("缺少 OpenAi ApiKey 配置");
        }

        return RandomUtil.randomEle(openAiApiKeyDOList);
    }
}
