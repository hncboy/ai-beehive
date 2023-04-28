package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import lombok.Data;

/**
 * 邮箱发送日志表实体类
 * 用于审计日志
 * @author CoDeleven
 */
@TableName(value = "sys_email_send_log")
@Data
public class SysEmailSendLogDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发件人邮箱
     */
    private String fromEmailAddress;

    /**
     * 收件人邮箱
     */
    private String toEmailAddress;

    /**
     * 业务类型
     */
    private EmailBizTypeEnum bizType;

    /**
     * 请求 ip
     */
    private String requestIp;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 发送状态，0成功，1失败
     */
    private Integer status;

    /**
     * 发送内容
     */
    private String messageId;

    /**
     * 发送后的消息，用于记录成功/失败的信息，成功默认为 success
     */
    private String message;

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