package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import lombok.Data;

/**
 * 前端用户登录日志表
 * @author CoDeleven
 */
@TableName(value ="sys_front_user_login_log")
@Data
public class SysFrontUserLoginLogDO implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 登录的基础用户ID
     */
    @TableField(value = "base_user_id")
    private Integer baseUserId;

    /**
     * 登录方式（注册方式），邮箱登录，手机登录等等
     */
    @TableField(value = "login_type")
    private FrontUserRegisterTypeEnum loginType;

    /**
     * 登录信息ID与login_type有关联，邮箱登录时关联front_user_extra_email
     */
    @TableField(value = "login_extra_info_id")
    private Integer loginExtraInfoId;

    /**
     * 登录的IP地址
     */
    @TableField(value = "login_ip")
    private String loginIp;

    /**
     * 登录状态，1登录成功，0登录失败
     */
    @TableField(value = "login_status")
    private Boolean loginStatus;

    /**
     * 登录后返回的消息
     */
    @TableField(value = "message")
    private String message;

    /**
     * 发生时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @Serial
    private static final long serialVersionUID = 1L;
}