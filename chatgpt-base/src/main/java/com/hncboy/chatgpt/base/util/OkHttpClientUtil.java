package com.hncboy.chatgpt.base.util;

import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import okhttp3.OkHttpClient;

import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author hncboy
 * @date 2023-4-11
 * OkHttpClient 工具
 */
public class OkHttpClientUtil {

    private static final Map<ApiTypeEnum, OkHttpClient> INSTANCE_MAP = new ConcurrentHashMap<>();

    private OkHttpClientUtil() {
        // private constructor to prevent instantiation from outside
    }

    /**
     * 获取一个单例 OkHttpClient 实例
     *
     * @param apiTypeEnum    Api 类型
     * @param connectTimeout 连接超时时间（毫秒）
     * @param readTimeout    读取超时时间（毫秒）
     * @param writeTimeout   写入超时时间（毫秒）
     * @param proxy          代理
     * @return OkHttpClient 实例
     */
    public static OkHttpClient getInstance(ApiTypeEnum apiTypeEnum, int connectTimeout, int readTimeout, int writeTimeout, Proxy proxy) {
        OkHttpClient instance = INSTANCE_MAP.get(apiTypeEnum);
        if (instance == null) {
            synchronized (OkHttpClientUtil.class) {
                instance = INSTANCE_MAP.get(apiTypeEnum);
                if (instance == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
                    if (proxy != null) {
                        builder.proxy(proxy);
                    }
                    instance = builder.build();
                    INSTANCE_MAP.put(apiTypeEnum, instance);
                }
            }
        }
        return instance;
    }
}
