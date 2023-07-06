package com.hncboy.beehive.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author CoDeleven
 * @date 2023/5/25
 * cell 配置项表实体类
 */
@TableName("bh_cell_config")
@Data
public class CellConfigDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * cell code
     */
    private String cellCode;

    /**
     * 名称
     */
    private String name;

    /**
     * 唯一编码，cell 中唯一
     */
    private String code;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 示例值
     */
    private String exampleValue;

    /**
     * 是否必填，false 否 true 是
     * <p>
     * 有些参数值系统未提供必须由用户自己提供
     */
    private Boolean isRequired;

    /**
     * 是否有默认值，false 否 true 是
     */
    private Boolean isHaveDefaultValue;

    /**
     * 用户是否可以使用默认值，false 否 true 是
     */
    private Boolean isUserCanUseDefaultValue;

    /**
     * 用户是否可见，false 否 true 是
     */
    private Boolean isUserVisible;

    /**
     * 用户是否可见默认值，false 否 true 是
     * <p>
     * 有些配置用户可修改但是看不到默认值
     * 前提：isUserVisible=true
     * 比如 OpenAi 的 ApiKey，用户可以使用系统提供的但是看不到默认值
     */
    private Boolean isUserValueVisible;

    /**
     * 用户是否可修改，false 否 true 是
     * <p>
     * 前提：isUserVisible=true
     * 有些配置用户可见但是不能修改
     */
    private Boolean isUserModifiable;

    /**
     * 用户创建房间后是否可修改，false 否 true 是
     * <p>
     * 有些参数创建好之后不允许修改，否则对话会出问题
     * 比如官方 ChatGPT 网页版逆向的模型，同一个房间的模型必须一致
     */
    private Boolean isUserLiveModifiable;

    /**
     * 介绍，用户端查看
     */
    private String introduce;

    /**
     * 备注，管理端查看
     */
    private String remark;

    /**
     * 前端组件类型
     */
    private String frontComponentType;

    /**
     * 前端組件内容
     */
    private String frontComponentContent;

    /**
     * 是否删除 0 否 NULL 是
     */
    @TableLogic(value = "0", delval = "NULL")
    private Boolean isDeleted;

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