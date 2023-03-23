package com.hncboy.chatgpt.config;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.enums.ApiTypeEnum;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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
    }
}
