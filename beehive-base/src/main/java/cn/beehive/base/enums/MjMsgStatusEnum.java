package cn.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 消息状态枚举
 */
@AllArgsConstructor
public enum MjMsgStatusEnum {

    /**
     * 系统排队上限
     */
    SYS_MAX_QUEUE(1),

    /**
     * 系统排队中
     */
    SYS_QUEUING(2),

    /**
     * 系统失败
     */
    SYS_FAILURE(3),

    /**
     * 系统成功
     */
    SYS_SUCCESS(4),

    /* 下面为进入 MJ 后的参数 */

    /**
     * 等待 MJ 接收消息
     */
    MJ_WAIT_RECEIVED(10),

    /**
     * MJ 执行中
     */
    MJ_IN_PROGRESS(11),

    /**
     * MJ 失败
     */
    MJ_FAILURE(12),

    /**
     * MJ 成功
     */
    MJ_SUCCESS(13);

    @Getter
    @EnumValue
    private final Integer code;
}
