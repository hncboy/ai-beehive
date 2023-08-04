package com.hncboy.beehive.base.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomChatGlmMsgStatusEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author hanpeng
 * @date 2023/8/4
 * ChatGLM 对话房间消息表
 */
@Data
@TableName("bh_room_chat_glm_msg")
public class RoomChatGlmMsgDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * 房间 id
     */
    private Long roomId;

    /**
     * 父级问题消息 id
     */
    private Long parentQuestionMessageId;

    /**
     * 消息类型枚举
     */
    private MessageTypeEnum messageType;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * ip
     */
    private String ip;

    /**
     * apiKey
     */
    private String apiKey;

    /**
     * 消息内容
     * 包含上下文的对话这里只会显示出用户发送的
     */
    private String content;

    /**
     * 消息的原始数据
     * 问题：请求参数
     * 回答：响应参数
     */
    private String originalData;

    /**
     * 错误响应数据
     */
    private String responseErrorData;

    /**
     * 输入消息的 tokens
     */
    private Integer promptTokens;

    /**
     * 输出消息的 tokens
     */
    private Integer completionTokens;

    /**
     * 累计 Tokens
     */
    private Integer totalTokens;

    /**
     * 消息状态
     */
    private RoomChatGlmMsgStatusEnum status;

    /**
     * 房间配置参数
     */
    private String roomConfigParamJson;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
