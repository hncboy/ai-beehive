package com.hncboy.beehive.base.domain.entity;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间表实体类
 */
@Data
@TableName("bh_room")
public class RoomDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * 颜色，十六进制
     */
    private String color;

    /**
     * 名称
     */
    private String name;

    /**
     * 固定时间戳
     */
    private Long pinTime;

    /**
     * ip
     */
    @TableField(fill = FieldFill.INSERT)
    private String ip;

    /**
     * cell code
     */
    private CellCodeEnum cellCode;

    /**
     * 是否删除 0 否 1 是
     */
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
