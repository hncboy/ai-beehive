package com.hncboy.beehive.base.util;

import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.beehive.base.config.ProxyConfig;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @author hncboy
 * @date 2023-4-11
 * OkHttpClient 工具
 */
public class OkHttpClientUtil {

    private static final Integer CONNECT_TIMEOUT = 60;
    private static final Integer READ_TIMEOUT = 60;
    private static final Integer WRITE_TIMEOUT = 60;

    private static final OkHttpClient INSTANCE = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS).build();
    private static volatile OkHttpClient PROXY_INSTANCE = null;

    private OkHttpClientUtil() {
        // private constructor to prevent instantiation from outside
    }

    /**
     * 获取 OkHttpClient 实例
     *
     * @return OkHttpClient 实例
     */
    public static OkHttpClient getInstance() {
        return INSTANCE;
    }

    /**
     * DCL 获取代理 OkHttpClient 实例
     *
     * @return OkHttpClient 实例
     */
    public static OkHttpClient getProxyInstance() {
        if (PROXY_INSTANCE == null) {
            synchronized (OkHttpClientUtil.class) {
                if (PROXY_INSTANCE == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
                    ProxyConfig proxyConfig = SpringUtil.getBean(ProxyConfig.class);
                    builder.proxy(proxyConfig.getProxy());
                    PROXY_INSTANCE = builder.build();
                }
            }
        }
        return PROXY_INSTANCE;
    }
}
