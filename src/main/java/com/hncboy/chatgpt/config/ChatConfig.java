package com.hncboy.chatgpt.config;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.enums.ApiTypeEnum;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/3/22 20:36
 * 聊天配置参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "chat")
public class ChatConfig implements InitializingBean {

    /**
     * 密码
     */
    private String authSecretKey;

    /**
     * OpenAI API Key - https://beta.openai.com/docs/api-reference/authentication
     */
    private String openaiApiKey;

    /**
     * OpenAI Access Token - https://beta.openai.com/docs/api-reference/authentication
     * Change this to an `accessToken` extracted from the ChatGPT site's `https://chat.openai.com/api/auth/session` response
     */
    private String openaiAccessToken;

    /**
     * OpenAI API Base URL - https://api.openai.com
     */
    private String openaiApiBaseUrl;

    /**
     * OpenAI API Model - https://beta.openai.com/docs/models
     */
    private String openaiApiModel;

    /**
     * Reverse Proxy
     */
    private String apiReverseProxy;

    /**
     * Timeout in milliseconds
     */
    private Integer timeoutMs;

    /**
     * Socks Proxy Host
     */
    private String socksProxyHost;

    /**
     * Socks Proxy Port
     */
    private Integer socksProxyPort;

    /**
     * HTTPS Proxy
     */
    private String httpsProxy;

    /**
     * 判断是否有 socks 代理
     *
     * @return true/false
     */
    public Boolean hasSocksProxy() {
        return StrUtil.isNotBlank(socksProxyHost) && Objects.nonNull(socksProxyPort);
    }

    /**
     * 判断是否有鉴权
     *
     * @return true/false
     */
    public Boolean hasAuth() {
        return StrUtil.isNotEmpty(getAuthSecretKey());
    }

    /**
     * 获取 API 类型枚举
     *
     * @return API 类型枚举
     */
    public ApiTypeEnum getApiTypeEnum() {
        // 优先 API KEY
        if (StrUtil.isNotBlank(openaiApiKey)) {
            return ApiTypeEnum.API_KEY;
        }
        return ApiTypeEnum.ACCESS_TOKEN;
    }

    @Override
    public void afterPropertiesSet() {
        if (StrUtil.isBlank(openaiApiKey) && StrUtil.isBlank(openaiAccessToken)) {
            throw new RuntimeException("apiKey 或 accessToken 必须有值");
        }

        if (StrUtil.isBlank(openaiApiBaseUrl)) {
            throw new RuntimeException("openaiApiBaseUrl 必须有值");
        }

        // 校验 apiModel
        if (StrUtil.isBlank(openaiApiModel)) {
            openaiApiModel = null;
            return;
        }

        ChatCompletion.Model[] models = ChatCompletion.Model.values();
        for (ChatCompletion.Model model : models) {
            if (model.getName().equals(openaiApiModel)) {
                return;
            }
        }
        throw new RuntimeException("apiMode 填写错误");
    }
}
