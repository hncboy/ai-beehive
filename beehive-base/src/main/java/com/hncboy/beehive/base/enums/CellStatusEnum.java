package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/5/29
 * cell 状态枚举
 */
@AllArgsConstructor
public enum CellStatusEnum {

    /**
     * 隐藏
     */
    HIDDEN("hidden"),

    /**
     * 开发中
     */
    CODING("coding"),

    /**
     * 修复中
     */
    FIXING("fixing"),

    /**
     * 已发布
     */
    PUBLISHED("published"),

    /**
     * 已关闭
     */
    CLOSED("closed"),

    /**
     * 待开发
     */
    WAIT_CODING("wait_coding");

    @Getter
    @EnumValue
    @JsonValue
    private final String desc;
}
