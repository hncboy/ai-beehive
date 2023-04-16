package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * 邮箱验证码核销记录表实体类
 * 记录某个邮箱发送了什么验证码，方便验证
 *
 * @author CoDeleven
 */
@TableName(value = "email_verify_code")
@Data
public class EmailVerifyCodeDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 验证码接收邮箱地址
     */
    private String toEmailAddress;

    /**
     * 验证码唯一
     */
    private String verifyCode;

    /**
     * 是否使用，false 否，true 是
     */
    private Boolean isUsed;

    /**
     * 核销IP，方便识别一些机器人账号
     */
    private String verifyIp;

    /**
     * 验证码过期时间
     */
    private Date expireAt;

    /**
     * 当前邮箱业务
     */
    private EmailBizTypeEnum bizType;

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