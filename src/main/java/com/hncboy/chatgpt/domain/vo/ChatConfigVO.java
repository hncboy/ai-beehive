package com.hncboy.chatgpt.domain.vo;

import com.hncboy.chatgpt.enums.ApiTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/3/22 23:26
 * 聊天配置展示参数
 */
@Data
@Schema(name = "聊天配置展示参数")
public class ChatConfigVO {

    @Schema(name = "API 类型")
    private ApiTypeEnum apiModel;

    @Schema(name = "余额")
    private String balance;

    @Schema(name = "HTTPS Proxy")
    private String httpsProxy;

    @Schema(name = "反向代理")
    private String reverseProxy;

    @Schema(name = "SOCKS Proxy")
    private String socksProxy;

    @Schema(name = "超时时间")
    private Integer timeoutMs;
}
