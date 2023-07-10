package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/6/27
 * 前端用户状态枚举
 */
@AllArgsConstructor
public enum FrontUserStatusEnum {

    /**
     * 正常状态
     */
    NORMAL("normal"),

    /**
     * 禁止访问
     */
    BLOCK("block"),

    /**
     * 待审核
     * 可以配置注册时是否需要审核
     */
    WAIT_CHECK("wait_check");

    @JsonValue
    @EnumValue
    @Getter
    private final String code;
}
