package cn.beehive.base.enums;

import lombok.AllArgsConstructor;

/**
 * @author hncboy
 * @date 2023/5/11
 * 系统参数 key 枚举
 */
@AllArgsConstructor
public enum SysParamKeyEnum {

    /**
     * 是否启用 Midjourney
     */
    ENABLE_MIDJOURNEY("enable_midjourney"),

    /**
     * 是否启用 HTTP 代理
     */
    ENABLE_PROXY("enable_http_proxy"),

    /**
     * HTTP 代理主机
     */
    HTTP_PROXY_HOST("http_proxy_host"),

    /**
     * HTTP 代理端口
     */
    HTTP_PROXY_PORT("http_proxy_port");

    /**
     * paramKey
     */
    private final String paramKey;

    /**
     * 获取 ParamKey
     *
     * @return ParamKey
     */
    public String getParamKey() {
        // 转小写
        return paramKey.toLowerCase();
    }
}
