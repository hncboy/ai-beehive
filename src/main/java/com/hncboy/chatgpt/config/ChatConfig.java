package com.hncboy.chatgpt.config;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.api.accesstoken.ConversationModelEnum;
import com.hncboy.chatgpt.enums.ApiTypeEnum;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
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
     * 反向代理地址，AccessToken 时用到
     */
    private String apiReverseProxy;

    /**
     * 超时毫秒
     */
    private Integer timeoutMs;

    /**
     * HTTP 代理主机
     */
    private String httpProxyHost;

    /**
     * HTTP 代理端口
     */
    private Integer httpProxyPort;

    /**
     * 判断是否有 http 代理
     *
     * @return true/false
     */
    public Boolean hasHttpProxy() {
        return StrUtil.isNotBlank(httpProxyHost) && Objects.nonNull(httpProxyPort);
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

        // ApiKey
        if (getApiTypeEnum() == ApiTypeEnum.API_KEY) {
            // apiBaseUrl 必须有值
            if (StrUtil.isBlank(openaiApiBaseUrl)) {
                throw new RuntimeException("openaiApiBaseUrl 必须有值");
            }

            // 校验 apiModel
            if (StrUtil.isBlank(openaiApiModel)) {
                openaiApiModel = ChatCompletion.Model.GPT_3_5_TURBO.getName();
                return;
            }
            ChatCompletion.Model[] models = ChatCompletion.Model.values();
            for (ChatCompletion.Model model : models) {
                if (model.getName().equals(openaiApiModel)) {
                    return;
                }
            }
            throw new RuntimeException("ApiKey apiModel 填写错误");
        }

        // AccessToken
        if (getApiTypeEnum() == ApiTypeEnum.ACCESS_TOKEN) {
            // apiReverseProxy 必须有值
            if (StrUtil.isBlank(apiReverseProxy)) {
                throw new RuntimeException("apiReverseProxy 必须有值");
            }

            // 校验 apiModel
            if (StrUtil.isBlank(openaiApiModel)) {
                openaiApiModel = ConversationModelEnum.DEFAULT_GPT_3_5.getName();
                return;
            }

            if (!ConversationModelEnum.NAME_MAP.containsKey(openaiApiKey)) {
                throw new RuntimeException("AccessToken apiModel 填写错误");
            }
        }
    }
}
