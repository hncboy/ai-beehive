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
 * 房间配置项参数表实体类
 */
@Data
@TableName("bh_room_config_param")
public class RoomConfigParamDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 房间 ID
     */
    private Long roomId;

    /**
     * 配置项 code
     */
    private String cellConfigCode;

    /**
     * 配置项值
     */
    private String value;

    /**
     * 用户用默认值就会删除之前配置的值
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