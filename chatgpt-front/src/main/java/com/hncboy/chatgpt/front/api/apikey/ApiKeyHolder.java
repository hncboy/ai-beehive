package com.hncboy.chatgpt.front.api.apikey;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.domain.entity.CurrentKeyDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserApiKeyRelDO;
import com.hncboy.chatgpt.front.service.CurrentKeyService;
import com.hncboy.chatgpt.front.service.FrontUserApiKeyRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author Jankin Wu
 * @description ApiKey 持有者
 * @date 2023/4/30 12:53
 */
@Component
public class ApiKeyHolder {

    private static FrontUserApiKeyRelService frontUserApiKeyRelService;

    private static CurrentKeyService currentKeyService;

    private static volatile ApiKeyHolder instance = null;

    @Autowired
    public void setFrontUserApiKeyRelService(FrontUserApiKeyRelService frontUserApiKeyRelService) {
        ApiKeyHolder.frontUserApiKeyRelService = frontUserApiKeyRelService;
    }

    @Autowired
    public void setCurrentKeyService(CurrentKeyService currentKeyService) {
        ApiKeyHolder.currentKeyService = currentKeyService;
    }

    private String currentApiKey;

    private ApiKeyHolder() {
    }

    public static ApiKeyHolder getInstance() {
        if (instance == null) {
            synchronized (ApiKeyHolder.class) {
                if (instance == null) {
                    instance = new ApiKeyHolder();
                }
            }
        }
        return instance;
    }

    /**
     * 获取用户Id对应的ApiKey, 如果当前用户未分配ApiKey，则轮流分配ApiKey给当前用户
     *
     * @return ApiKey
     */
    public String get() {
        int userId = NumberUtil.parseInt(String.valueOf(StpUtil.getLoginId()));
        FrontUserApiKeyRelDO one = frontUserApiKeyRelService.getOne(new QueryWrapper<FrontUserApiKeyRelDO>().lambda().eq(FrontUserApiKeyRelDO::getUserId, userId));
        if (Objects.nonNull(one)) {
            return one.getApiKey();
        }
        String apiKey = getNextApiKey();
        frontUserApiKeyRelService.save(FrontUserApiKeyRelDO.builder().apiKey(apiKey).userId(userId).build());
        return apiKey;
    }

    private String getNextApiKey() {
        ChatConfig chatConfig = SpringUtil.getBean(ChatConfig.class);
        List<String> apiKeyList = chatConfig.getOpenaiApiKey();
        CurrentKeyDO currentKeyDO = currentKeyService.getOne(new QueryWrapper<CurrentKeyDO>().lambda().last("limit 1"));
        if (Objects.isNull(currentKeyDO)) {
            currentKeyService.save(CurrentKeyDO.builder().currentKey(apiKeyList.get(0)).build());
            return apiKeyList.get(0);
        }
        for (int i = 0; i < apiKeyList.size(); i++) {
            String n = apiKeyList.get(i);
            if (currentKeyDO.getCurrentKey().equals(n)) {
                if (apiKeyList.size() >= i + 1) {
                    currentApiKey = apiKeyList.get(i + 1);
                } else {
                    currentApiKey = apiKeyList.get(0);
                }
                currentKeyDO.setCurrentKey(currentApiKey);
                currentKeyService.updateById(currentKeyDO);
                return currentApiKey;
            }
        }
        return null;
    }

}
