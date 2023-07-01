package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/6/30
 * OpenAi ApiKey 使用场景枚举
 */
@AllArgsConstructor
public enum OpenAiApiKeyUseSceneEnum {

    /**
     * GPT 3.5
     */
    GPT_3_5("GPT3.5"),

    /**
     * GPT 4
     */
    GPT_4("GPT4"),

    /**
     * 绘图
     */
    IMAGE("IMAGE");

    @EnumValue
    @Getter
    private final String code;
}
