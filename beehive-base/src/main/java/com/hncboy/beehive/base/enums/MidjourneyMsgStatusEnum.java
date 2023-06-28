package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 消息状态枚举
 * 中间状态：表示状态可能变更
 * 结束状态：状态不会变更
 */
@AllArgsConstructor
public enum MidjourneyMsgStatusEnum {

    /**
     * （最终状态）系统排队上限
     */
    SYS_MAX_QUEUE(1),

    /**
     * （中间状态）系统排队中
     */
    SYS_QUEUING(2),

    /**
     * （最终状态）系统成功
     */
    SYS_SUCCESS(3),

    /**
     * （最终状态）系统失败
     */
    SYS_FAILURE(4),

    /**
     * （最终状态）系统等待 MJ 接收消息失败
     */
    SYS_WAIT_MJ_RECEIVED_FAILURE(5),

    /**
     * （最终状态）系统发送 MJ 请求失败
     */
    SYS_SEND_MJ_REQUEST_FAILURE(6),

    /**
     * （最终状态）系统完成 MJ 执行中任务失败
     */
    SYS_FINISH_MJ_IN_PROGRESS_FAILURE(7),

    /* 下面为进入 MJ 后的参数 */

    /**
     * （中间状态）等待 MJ 接收消息
     */
    MJ_WAIT_RECEIVED(20),

    /**
     * （中间状态）MJ 执行中
     */
    MJ_IN_PROGRESS(21),

    /**
     * （最终状态）MJ 失败
     */
    MJ_FAILURE(22),

    /**
     * （最终状态）MJ 成功
     */
    MJ_SUCCESS(23);

    @Getter
    @EnumValue
    private final Integer code;
}