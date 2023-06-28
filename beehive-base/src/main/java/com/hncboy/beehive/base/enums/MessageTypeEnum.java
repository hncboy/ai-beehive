package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023-3-26
 * 消息类型枚举
 */
@AllArgsConstructor
public enum MessageTypeEnum {

    /**
     * 问题
     */
    QUESTION(1, "question"),

    /**
     * 回答
     */
    ANSWER(2, "answer");

    @Getter
    @EnumValue
    private final Integer code;

    @Getter
    @JsonValue
    private final String message;
}
