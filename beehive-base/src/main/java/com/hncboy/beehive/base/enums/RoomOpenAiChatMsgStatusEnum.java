package com.hncboy.beehive.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hncboy
 * @date 2023-3-25
 * OpenAi 对话房间消息状态枚举
 */
@AllArgsConstructor
public enum RoomOpenAiChatMsgStatusEnum {

    /**
     * 针对问题
     * 初始化，未发送
     * 因为发送和接收的速度很快，所以这种初始状态基本上不会有，除非异常情况
     */
    INIT(0),

    /**
     * 针对问题和回答
     * 因为是流式回答，所以有部分成功
     */
    PART_SUCCESS(1),

    /**
     * 针对问题和回答
     * 消息全部接收完毕
     */
    COMPLETE_SUCCESS(2),

    /**
     * 针对问题和回答
     * 消息发送失败
     */
    ERROR(3),

    /**
     * 发送问题
     * 问题 Token 超过指定模型上限
     */
    EXCEPTION_TOKEN_EXCEED_LIMIT(4),

    /**
     * 发送问题
     * 内容审核不通过
     */
    CONTENT_CHECK_FAILURE(5);

    @Getter
    @EnumValue
    private final Integer code;
}
