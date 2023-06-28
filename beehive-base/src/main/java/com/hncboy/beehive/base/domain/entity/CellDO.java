package com.hncboy.beehive.base.domain.entity;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.enums.CellStatusEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author CoDeleven
 * @date 2023/5/25
 * cell 表实体类
 */
@Data
@TableName("bh_cell")
public class CellDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 封面
     */
    private String imageUrl;

    /**
     * 唯一编码
     */
    private CellCodeEnum code;

    /**
     * 排序，值大的排前面
     */
    private Integer sort;

    /**
     * 状态
     */
    private CellStatusEnum status;

    /**
     * 介绍
     */
    private String introduce;

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