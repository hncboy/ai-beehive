package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.hncboy.chatgpt.base.enums.UserExtraBindingTypeEnum;
import lombok.Data;

/**
 * 前端用户绑定表
 * @author CoDeleven
 */
@TableName(value ="front_user_extra_binding")
@Data
public class FrontUserExtraBindingDO implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 绑定类型,qq,wechat,sina,github,email,phone
     */
    @TableField(value = "binding_type")
    private UserExtraBindingTypeEnum bindingType;

    /**
     * 额外信息表的用户ID
     */
    @TableField(value = "extra_info_id")
    private Integer extraInfoId;

    /**
     * 基础用户表的ID
     */
    @TableField(value = "base_user_id")
    private Integer baseUserId;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}