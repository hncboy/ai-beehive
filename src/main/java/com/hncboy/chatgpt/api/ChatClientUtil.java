package com.hncboy.chatgpt.api;

import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.chatgpt.config.ChatConfig;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import lombok.experimental.UtilityClass;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author hncboy
 * @date 2023/3/24 16:09
 * 聊天客户端工具
 */
@UtilityClass
public class ChatClientUtil {

    /**
     * 构建 API 流式请求客户端
     *
     * @return OpenAiStreamClient
     */
    public OpenAiStreamClient buildOpenAiStreamClient() {
        ChatConfig chatConfig = SpringUtil.getBean(ChatConfig.class);
        return OpenAiStreamClient.builder()
                .connectTimeout(chatConfig.getTimeoutMs())
                .readTimeout(chatConfig.getTimeoutMs())
                .writeTimeout(chatConfig.getTimeoutMs())
                .apiKey(chatConfig.getOpenaiApiKey())
                .proxy(getProxy())
                .apiHost(chatConfig.getOpenaiApiBaseUrl())
                .build();
    }

    /**
     * 构建 API 请求客户端
     *
     * @return OpenAiClient
     */
    public OpenAiClient buildOpenAiClient() {
        ChatConfig chatConfig = SpringUtil.getBean(ChatConfig.class);
        return OpenAiClient.builder()
                .connectTimeout(chatConfig.getTimeoutMs())
                .readTimeout(chatConfig.getTimeoutMs())
                .writeTimeout(chatConfig.getTimeoutMs())
                .apiKey(chatConfig.getOpenaiApiKey())
                .proxy(getProxy())
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
