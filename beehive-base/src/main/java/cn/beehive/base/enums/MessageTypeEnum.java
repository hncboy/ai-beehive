package cn.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
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
    QUESTION(1),

    /**
     * 回答
     */
    ANSWER(2);

    @Getter
    @EnumValue
    private final Integer code;
}
