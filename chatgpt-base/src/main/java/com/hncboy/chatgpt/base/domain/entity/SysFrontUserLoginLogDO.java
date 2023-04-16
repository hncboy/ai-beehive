package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * 前端用户登录日志表实体类
 *
 * @author CoDeleven
 */
@TableName(value = "sys_front_user_login_log")
@Data
public class SysFrontUserLoginLogDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 登录的基础用户ID
     */
    private Integer baseUserId;

    /**
     * 登录方式（注册方式），邮箱登录，手机登录等等
     */
    private FrontUserRegisterTypeEnum loginType;

    /**
     * 登录信息ID与login_type有关联，邮箱登录时关联front_user_extra_email
     */
    private Integer loginExtraInfoId;

    /**
     * 登录的IP地址
     */
    private String loginIp;

    /**
     * 登录状态，1登录成功，0登录失败
     */
    private Boolean loginStatus;

    /**
     * 登录后返回的消息
     */
    private String message;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}