package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import lombok.Data;

/**
 * 邮箱发送日志
 * @author CoDeleven
 */
@TableName(value ="sys_email_send_log")
@Data
public class SysEmailSendLogDO implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发件人
     */
    @TableField(value = "from_email_address")
    private String fromEmailAddress;

    /**
     * 收件人
     */
    @TableField(value = "to_email_address")
    private String toEmailAddress;

    /**
     * 业务类型
     */
    @TableField(value = "biz_type")
    private EmailBizTypeEnum bizType;

    /**
     * 发送内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 发送状态，0成功，1失败
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 发送内容
     */
    @TableField(value = "message_id")
    private String messageId;

    /**
     * 发送后的消息，用于记录成功/失败的信息，成功默认为success
     */
    @TableField(value = "message")
    private String message;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 操作人用户ID，预留字段，默认为1
     */
    @TableField(value = "operator_sys_user_id")
    private Integer operatorSysUserId;

    @Serial
    private static final long serialVersionUID = 1L;
}