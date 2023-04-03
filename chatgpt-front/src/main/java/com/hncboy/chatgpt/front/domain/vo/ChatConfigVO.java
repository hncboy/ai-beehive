package com.hncboy.chatgpt.front.domain.vo;

import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/3/22 23:26
 * 聊天配置展示参数
 */
@Data
@Schema(title = "聊天配置展示参数")
public class ChatConfigVO {

    @Schema(title = "API 类型")
    private ApiTypeEnum apiModel;

    @Schema(title = "余额")
    private String balance;

    @Schema(title = "HTTPS Proxy")
    private String httpsProxy;

    @Schema(title = "反向代理")
    private String reverseProxy;

    @Schema(title = "SOCKS Proxy")
    private String socksProxy;

    @Schema(title = "超时时间")
    private Integer timeoutMs;
}
