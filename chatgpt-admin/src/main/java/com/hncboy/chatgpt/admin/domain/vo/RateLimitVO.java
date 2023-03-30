package com.hncboy.chatgpt.admin.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/3/30 18:10
 * 限流处展示参数
 */
@Data
@Schema(title = "限流处理器展示参数")
public class RateLimitVO {

    @Schema(title = "ip")
    private String ip;

    @Schema(title = "限流规则")
    private String limitRule;

    @Schema(title = "时间内已经发送次数")
    private Integer alreadySendCount;

    @Schema(title = "是否被限流")
    private Boolean isLimited;

    @Schema(title = "下次可以发送消息的时间")
    private String nextSendTime;
}
