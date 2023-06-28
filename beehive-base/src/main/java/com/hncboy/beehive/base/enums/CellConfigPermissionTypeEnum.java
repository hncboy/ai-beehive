package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/6/2
 * Cell 配置项权限类型枚举
 */
@AllArgsConstructor
public enum CellConfigPermissionTypeEnum {

    /**
     * 可以使用默认值
     */
    CAN_USER_DEFAULT_VALUE(1);

    @Getter
    @EnumValue
    private final Integer code;
}
