package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端用户邮箱登录
 * @author CoDeleven
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value ="front_user_extra_email")
@Data
public class FrontUserExtraEmailDO implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 邮箱账号
     */
    @TableField(value = "username")
    private String username;

    /**
     * 加密后的密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 加密盐
     */
    @TableField(value = "salt")
    private String salt;

    /**
     * 是否验证过
     */
    @TableField(value = "verified")
    private Boolean verified;

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