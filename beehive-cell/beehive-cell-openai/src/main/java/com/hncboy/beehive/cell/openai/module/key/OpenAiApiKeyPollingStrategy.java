package com.hncboy.beehive.cell.openai.module.key;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.OpenAiApiKeyDO;
import com.hncboy.beehive.base.enums.OpenAiApiKeyStatusEnum;
import com.hncboy.beehive.base.enums.OpenAiApiKeyUseSceneEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.util.RedisUtil;
import com.hncboy.beehive.cell.openai.enums.OpenAiApiKeyStrategyEnum;
import com.hncboy.beehive.cell.openai.service.OpenAiApiKeyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/7/2
 * OpenAi ApiKey 轮询策略
 * 按权重和 id 降序轮询
 */
@Component
public class OpenAiApiKeyPollingStrategy implements OpenAiApiKeyStrategy {

    @Resource
    private OpenAiApiKeyService openAiApiKeyService;

    /**
     * 轮询的 Redis Key 前缀
     */
    private static final String PREFIX_REDIS_KEY = "openai:key:polling:";

    /**
     * 轮询的 Redis Key 锁前缀
     */
    private static final String PREFIX_REDIS_LOCK_KEY = "openai:key:lock:polling:";

    @Override
    public OpenAiApiKeyStrategyEnum getStrategyEnum() {
        return OpenAiApiKeyStrategyEnum.POLLING;
    }

    @Override
    public OpenAiApiKeyDO getApiKeyInfo(OpenAiApiKeyUseSceneEnum useSceneEnum) {
        // 查询全部符合条件的
        List<OpenAiApiKeyDO> openAiApiKeyDOList = openAiApiKeyService.list(new LambdaQueryWrapper<OpenAiApiKeyDO>()
                // 查询启用的
                .eq(OpenAiApiKeyDO::getStatus, OpenAiApiKeyStatusEnum.ENABLE)
                // 查询指定使用场景的
                .like(OpenAiApiKeyDO::getUseScenes, useSceneEnum.getCode())
                // 按权重降序
                .orderByDesc(OpenAiApiKeyDO::getWeight)
                // 按 id 降序
                .orderByDesc(OpenAiApiKeyDO::getId));
        if (CollectionUtil.isEmpty(openAiApiKeyDOList)) {
            throw new ServiceException("缺少 OpenAi ApiKey 配置");
        }

        // 让切面生效
        return SpringUtil.getBean(OpenAiApiKeyPollingStrategy.class)
                .lockGetApiKeyInfo(useSceneEnum.getCode(), openAiApiKeyDOList);
    }

    /**
     * 锁获取 ApiKey 信息
     *
     * @param useSceneEnumCode   场景枚举 code
     * @param openAiApiKeyDOList key 列表
     * @return key 信息
     */
    @Lock4j(name = PREFIX_REDIS_LOCK_KEY, keys = "#useSceneEnumCode", expire = 60000, acquireTimeout = 3000)
    public OpenAiApiKeyDO lockGetApiKeyInfo(String useSceneEnumCode, List<OpenAiApiKeyDO> openAiApiKeyDOList) {
        String redisKey = PREFIX_REDIS_KEY + useSceneEnumCode;

        // 转 Map
        Map<Integer, OpenAiApiKeyDO> openAiApiKeyDOMap = openAiApiKeyDOList.stream().collect(Collectors.toMap(OpenAiApiKeyDO::getId, Function.identity()));

        // 获取 id
        String openAiApiKeyId = RedisUtil.get(redisKey);

        // 当前使用的下标
        int currentUseIndex = 0;
        // 当前使用的对象
        OpenAiApiKeyDO currentUseOpenAiApiKeyDO;

        // 不存在 id 的情况 或 存在 id 但是 id 现在无效的情况重新获取重新获取
        if (StrUtil.isBlank(openAiApiKeyId) || !openAiApiKeyDOMap.containsKey(Integer.valueOf(openAiApiKeyId))) {
            currentUseOpenAiApiKeyDO = openAiApiKeyDOList.get(currentUseIndex);
        } else {
            currentUseOpenAiApiKeyDO = openAiApiKeyDOMap.get(Integer.valueOf(openAiApiKeyId));
            // 获取对应的下标
            for (int i = 0; i < openAiApiKeyDOList.size(); i++) {
                if (Objects.equals(openAiApiKeyDOList.get(i).getId(), Integer.valueOf(openAiApiKeyId))) {
                    currentUseIndex = i;
                    break;
                }
            }
        }

        // 获取下一个轮询的 id
        OpenAiApiKeyDO nextOpenAiApiKeyDO = openAiApiKeyDOList.get((currentUseIndex + 1) % openAiApiKeyDOList.size());
        // 放入下一个轮询到的 id
        RedisUtil.set(redisKey, String.valueOf(nextOpenAiApiKeyDO.getId()));

        return currentUseOpenAiApiKeyDO;
    }
}
