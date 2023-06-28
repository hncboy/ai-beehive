package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/6/3
 * 消息状态枚举
 */
@AllArgsConstructor
public enum MessageStatusEnum {

    /**
     * 初始化
     */
    INIT(0),

    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 失败
     */
    FAILURE(2);

    @Getter
    @EnumValue
    private final Integer code;
}
