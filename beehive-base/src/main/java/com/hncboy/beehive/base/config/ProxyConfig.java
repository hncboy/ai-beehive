package com.hncboy.beehive.base.config;

import com.hncboy.beehive.base.exception.ServiceException;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;


/**
 * @author hncboy
 * @date 2023/5/31
 * 代理配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig implements InitializingBean {

    /**
     * 代理是否启用
     */
    private Boolean enabled;

    /**
     * HTTP 代理主机
     */
    private String httpHost;

    /**
     * HTTP 代理端口
     */
    private Integer httpPort;

    /**
     * 获取代理
     */
    public Proxy getProxy() {
        // 国内需要代理
        Proxy proxy = Proxy.NO_PROXY;
        if (enabled) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpHost, httpPort));
        }
        return proxy;
    }

    @Override
    public void afterPropertiesSet() {
        if (enabled) {
            if (httpHost == null || httpPort == null) {
                throw new ServiceException("代理配置不正确");
            }
        }
    }
}
