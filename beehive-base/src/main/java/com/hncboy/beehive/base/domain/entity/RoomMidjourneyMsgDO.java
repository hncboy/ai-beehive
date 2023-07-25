package com.hncboy.beehive.base.domain.entity;

import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgActionEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 房间消息表实体类
 */
@Data
@TableName("bh_room_midjourney_msg")
public class RoomMidjourneyMsgDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 房间 id
     */
    private Long roomId;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * 消息类型
     */
    private MessageTypeEnum type;

    /**
     * 用户输入
     */
    private String prompt;

    /**
     * 最终的输入
     */
    private String finalPrompt;

    /**
     * 响应内容
     */
    private String responseContent;

    /**
     * 指令动作
     */
    private MidjourneyMsgActionEnum action;

    /**
     * 压缩图名称
     */
    private String compressedImageName;

    /**
     * 原图名称
     */
    private String originalImageName;

    /**
     * uv 指令的父消息 id
     */
    private Long uvParentId;

    /**
     * u 指令使用比特位
     * 末尾 4 位 0000
     * 分别表示 U1 U2 U3 U4
     */
    private Integer uUseBit;

    /**
     * uv 位置
     */
    private Integer uvIndex;

    /**
     * 状态
     */
    private MidjourneyMsgStatusEnum status;

    /**
     * discord 开始时间
     */
    private Date discordStartTime;

    /**
     * discord 结束时间
     */
    private Date discordFinishTime;

    /**
     * discord message id
     */
    private String discordMessageId;

    /**
     * discord 频道 id
     */
    private String discordChannelId;

    /**
     * discord 图片地址
     */
    private String discordImageUrl;

    /**
     * 失败原因
     */
    private String failureReason;

    /**
     * 是否删除
     */
    private Boolean isDeleted;

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
