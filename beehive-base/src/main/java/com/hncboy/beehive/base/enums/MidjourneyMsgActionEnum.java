package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/5/19
 * Midjourney 动作枚举
 */
@AllArgsConstructor
public enum MidjourneyMsgActionEnum {

    /**
     * 生成图片
     */
    IMAGINE("imagine"),

    /**
     * 选中放大
     */
    UPSCALE("upscale"),

    /**
     * 选中其中的一张图，生成四张相似的
     */
    VARIATION("variation"),

    /**
     * 图转 prompt
     */
    DESCRIBE("describe");

    @Getter
    @EnumValue
    private final String action;
}
