package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端用户 基础表
 *
 * @author CoDeleven
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value ="front_user_base")
@Data
public class FrontUserBaseDO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;
    /**
     * 用户描述
     */
    @TableField("description")
    private String description;
    /**
     * 头像版本
     */
    @TableField("avatar_version")
    private Integer avatarVersion;
    /**
     * 最后一次登录IP
     */
    @TableField("last_login_ip")
    private Integer lastLoginIp;
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}