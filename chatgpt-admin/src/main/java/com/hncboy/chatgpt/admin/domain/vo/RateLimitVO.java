package com.hncboy.chatgpt.admin.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023-3-30
 * 限流处展示参数
 */
@Data
@Schema(title = "限流处理器展示参数")
public class RateLimitVO {

    @Schema(title = "ip")
    private String ip;

    @Schema(title = "ip 限流规则")
    private String ipLimitRule;

    @Schema(title = "全局限流规则")
    private String globalLimitRule;

    @Schema(title = "是否被 ip 限流")
    private Boolean isIpLimited;

    @Schema(title = "是否被全局限流")
    private Boolean isGlobalLimited;

    @Schema(title = "ip 限制时间内已发送次数")
    private Integer alreadySendCount;

    @Schema(title = "下次可以发送消息的时间")
    private String nextSendTime;
}
