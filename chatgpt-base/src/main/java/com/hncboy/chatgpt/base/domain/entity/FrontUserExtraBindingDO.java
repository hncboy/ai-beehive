package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hncboy.chatgpt.base.enums.UserExtraBindingTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * 前端用户绑定表实体类
 * 记录了 基础用户 和 登录方式 的绑定关系
 *
 * @author CoDeleven
 */
@TableName(value = "front_user_extra_binding")
@Data
public class FrontUserExtraBindingDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 绑定类型
     */
    private UserExtraBindingTypeEnum bindingType;

    /**
     * 额外信息表的用户ID
     */
    private Integer extraInfoId;

    /**
     * 基础用户表的ID
     */
    private Integer baseUserId;

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