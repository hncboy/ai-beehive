package com.hncboy.chatgpt.base.config;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.enums.ApiKeyModelEnum;
import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import com.hncboy.chatgpt.base.enums.ConversationModelEnum;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-22
 * 聊天配置参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "chat")
public class ChatConfig implements InitializingBean {

    /**
     * OpenAI API Key
     *
     * @link <a href="https://beta.openai.com/docs/api-reference/authentication"/>
     */
    private String openaiApiKey;

    /**
     * Access Token
     *
     * @link <a href="https://chat.openai.com/api/auth/session"/>
     */
    private String openaiAccessToken;

    /**
     * OpenAI API Base URL
     *
     * @link <a href="https://api.openai.com"/>
     */
    private String openaiApiBaseUrl;

    /**
     * OpenAI API Model
     *
     * @link <a href="https://beta.openai.com/docs/models"/>
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
     * 管理端账号
     */
    private String adminAccount;

    /**
     * 管理端密码
     */
    private String adminPassword;

    /**
     * 全局时间内最大请求次数，默认无限制
     */
    private Integer maxRequest;

    /**
     * 全局最大请求时间间隔（秒）
     */
    private Integer maxRequestSecond;

    /**
     * ip 时间内最大请求次数，默认无限制
     */
    private Integer ipMaxRequest;

    /**
     * ip 最大请求时间间隔（秒）
     */
    private Integer ipMaxRequestSecond;

    /**
     * 限制上下文对话的问题数量，默认不限制
     */
    private Integer limitQuestionContextCount;

    /**
     * 判断是否有 http 代理
     *
     * @return true/false
     */
    public Boolean hasHttpProxy() {
        return StrUtil.isNotBlank(httpProxyHost) && Objects.nonNull(httpProxyPort);
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

    /**
     * 获取全局时间内最大请求次数
     *
     * @return 最大请求次数
     */
    public Integer getMaxRequest() {
        return Opt.ofNullable(maxRequest).orElse(0);
    }

    /**
     * 获取全局最大请求时间间隔（秒）
     *
     * @return 时间间隔
     */
    public Integer getMaxRequestSecond() {
        return Opt.ofNullable(maxRequestSecond).orElse(0);
    }

    /**
     * 获取 ip 时间内最大请求次数
     *
     * @return 最大请求次数
     */
    public Integer getIpMaxRequest() {
        return Opt.ofNullable(ipMaxRequest).orElse(0);
    }

    /**
     * 获取 ip 最大请求时间间隔（秒）
     *
     * @return 时间间隔
     */
    public Integer getIpMaxRequestSecond() {
        return Opt.ofNullable(ipMaxRequestSecond).orElse(0);
    }

    /**
     * 获取限制的上下文对话数量
     *
     * @return 限制数量
     */
    public Integer getLimitQuestionContextCount() {
        return Opt.ofNullable(limitQuestionContextCount).orElse(0);
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
                openaiApiModel = ApiKeyModelEnum.GPT_3_5_TURBO.getName();
                return;
            }
            for (String modelName : ApiKeyModelEnum.NAME_MAP.keySet()) {
                if (openaiApiModel.equals(modelName)) {
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

            if (!ConversationModelEnum.NAME_MAP.containsKey(openaiApiModel)) {
                throw new RuntimeException("AccessToken apiModel 填写错误");
            }
        }
    }
}
