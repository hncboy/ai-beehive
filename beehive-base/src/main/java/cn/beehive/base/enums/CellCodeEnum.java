package cn.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 编码枚举
 */
@AllArgsConstructor
public enum CellCodeEnum {

    /**
     * OPENAI_CHAT
     */
    OPENAI_CHAT("openai_chat"),

    /**
     * NEW_BING
     */
    NEW_BING("new_bing"),

    /**
     * Midjourney
     */
    MIDJOURNEY("Midjourney");

    @Getter
    @JsonValue
    @EnumValue
    private final String code;
}
