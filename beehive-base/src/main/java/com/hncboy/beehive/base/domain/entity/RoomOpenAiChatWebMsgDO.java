package com.hncboy.beehive.base.domain.entity;

import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.RoomOpenAiChatMsgStatusEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/6/1
 * OpenAi 对话 Web 房间消息表
 */
@Data
@TableName("bh_room_openai_chat_web_msg")
public class RoomOpenAiChatWebMsgDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 请求的 messageId
     */
    private String requestMessageId;

    /**
     * 请求的 conversationId
     */
    private String requestConversationId;

    /**
     * 请求的 parentMessageId
     */
    private String requestParentMessageId;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * 房间 id
     */
    private Long roomId;

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
    @TableField(fill = FieldFill.INSERT)
    private String ip;

    /**
     * 消息内容
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
     * 消息状态
     */
    private RoomOpenAiChatMsgStatusEnum status;

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
