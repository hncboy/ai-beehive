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
 * 邮箱验证码核销记录表，记录某个邮箱发送了什么验证码，方便验证
 * @author CoDeleven
 */
@TableName(value ="email_verify_code")
@Data
public class EmailVerifyCodeDO implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 验证码接收邮箱地址
     */
    @TableField(value = "to_email_address")
    private String toEmailAddress;

    /**
     * 验证码
     */
    @TableField(value = "verify_code")
    private String verifyCode;

    /**
     * 使用状态，0表示未使用，1表示已使用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 核销IP，方便识别一些机器人账号
     */
    @TableField(value = "verify_ip")
    private String verifyIp;

    /**
     * 该条验证码何时过期，一般是发送时间+15分钟
     */
    @TableField(value = "expire_at")
    private Date expireAt;

    /**
     * 当前邮箱业务
     */
    @TableField(value = "biz_type")
    private EmailBizTypeEnum bizType;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}