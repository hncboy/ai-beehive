package com.hncboy.beehive.cell.wxqf.module.chat.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话 ERNIE-Bot API 请求参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WxqfChatErnieBotApiRequest extends WxqfChatApiCommonRequest {

    /**
     * 温度说明：
     * （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
     * （2）默认 0.95，范围 (0, 1.0]，不能为 0
     * （3）建议该参数和 top_p 只设置1个
     * （4）建议 top_p 和 temperature 不要同时更改
     */

    private BigDecimal temperature;

    /**
     * 说明：
     * （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
     * （2）默认 0.95，范围 (0, 1.0]，不能为 0
     * （3）建议该参数和 top_p 只设置1个
     * （4）建议 top_p 和 temperature 不要同时更改
     */
    @JsonProperty("top_p")
    private BigDecimal topP;

    /**
     * 通过对已生成的 token 增加惩罚，减少重复生成的现象。说明：
     * （1）值越大表示惩罚越大
     * （2）默认1.0，取值范围：[1.0, 2.0]
     */
    @JsonProperty("penaltyScore")
    private BigDecimal penaltyScore;
}
