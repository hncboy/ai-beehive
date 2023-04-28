package com.hncboy.chatgpt.front.api.apikey;

import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import com.hncboy.chatgpt.base.util.OkHttpClientUtil;
import com.unfbx.chatgpt.OpenAiStreamClient;
import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;

/**
 * @author hncboy
 * @date 2023-3-24
 * ApiKey 聊天 Client 构建者
 */
@UtilityClass
public class ApiKeyChatClientBuilder {

    /**
     * 构建 API 流式请求客户端
     *
     * @return OpenAiStreamClient
     */
    public OpenAiStreamClient buildOpenAiStreamClient() {
        ChatConfig chatConfig = SpringUtil.getBean(ChatConfig.class);

        OkHttpClient okHttpClient = OkHttpClientUtil.getInstance(ApiTypeEnum.API_KEY, chatConfig.getTimeoutMs(),
                chatConfig.getTimeoutMs(), chatConfig.getTimeoutMs(), getProxy());

        return OpenAiStreamClient.builder()
                .okHttpClient(okHttpClient)
                .apiKey(Collections.singletonList(chatConfig.getOpenaiApiKey()))
                .apiHost(chatConfig.getOpenaiApiBaseUrl())
                .build();
    }

    /**
     * 获取 Proxy
     *
     * @return Proxy
     */
    private Proxy getProxy() {
        ChatConfig chatConfig = SpringUtil.getBean(ChatConfig.class);
        // 国内需要代理
        Proxy proxy = Proxy.NO_PROXY;
        if (chatConfig.hasHttpProxy()) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(chatConfig.getHttpProxyHost(), chatConfig.getHttpProxyPort()));
        }
        return proxy;
    }
}
