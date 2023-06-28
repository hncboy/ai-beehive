package com.hncboy.beehive.base.domain.entity;

import com.hncboy.beehive.base.enums.MessageStatusEnum;
import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/6/3
 * OpenAi 图像房间消息表
 */
@Data
@TableName("bh_room_openai_image_msg")
public class RoomOpenAiImageMsgDO {

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
     * ip
     */
    @TableField(fill = FieldFill.INSERT)
    private String ip;

    /**
     * apiKey
     */
    private String apiKey;

    /**
     * 输入内容
     */
    private String prompt;

    /**
     * 尺寸大小
     */
    private String size;

    /**
     * openai 图像 url
     */
    private String openaiImageUrl;

    /**
     * 图像名称
     */
    private String imageName;

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
     * 消息状态枚举
     */
    private MessageStatusEnum status;

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
