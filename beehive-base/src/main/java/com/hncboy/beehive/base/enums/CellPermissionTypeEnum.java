package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/6/1
 * Cell 权限类型枚举
 */
@AllArgsConstructor
public enum CellPermissionTypeEnum {

    /**
     * 浏览
     */
    BROWSE(1),

    /**
     * 使用
     */
    USE(2);

    @Getter
    @EnumValue
    private final Integer code;
}
