package cn.beehive.base.resource.aip;

import lombok.Data;

/**
 * @author hncboy
 * @date 2023/6/6
 * @see <href="https://console.bce.baidu.com/"/>
 * 百度 AI 配置参数
 */
@Data
public class BaiduAipProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * appId
     */
    private String appId;

    /**
     * appKey
     */
    private String appKey;

    /**
     * secretKey
     */
    private String secretKey;
}
