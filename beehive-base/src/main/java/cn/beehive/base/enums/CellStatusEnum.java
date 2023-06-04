package cn.beehive.base.enums;

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
    HIDDEN(0, "hidden"),

    /**
     * 开发中
     */
    CODING(1, "coding"),

    /**
     * 修复中
     */
    FIXING(2, "fixing"),

    /**
     * 已发布
     */
    PUBLISHED(3, "published"),

    /**
     * 已关闭
     */
    CLOSED(4, "closed");

    @Getter
    @EnumValue
    private final Integer code;

    @Getter
    @JsonValue
    private final String desc;
}
