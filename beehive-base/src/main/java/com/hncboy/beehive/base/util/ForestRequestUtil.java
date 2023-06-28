package com.hncboy.beehive.base.util;

import com.hncboy.beehive.base.config.ProxyConfig;
import cn.hutool.extra.spring.SpringUtil;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author hncboy
 * @date 2023/5/24
 * ForestRequestUtil
 */
public class ForestRequestUtil {

    /**
     * 构建代理
     *
     * @param forestRequest 请求
     */
    public static void buildProxy(ForestRequest<?> forestRequest) {
        ProxyConfig proxyConfig = SpringUtil.getBean(ProxyConfig.class);
        if (!proxyConfig.getEnabled())  {
            return;
        }
        forestRequest.proxy(new ForestProxy(proxyConfig.getHttpHost(), proxyConfig.getHttpPort()));
    }
}
