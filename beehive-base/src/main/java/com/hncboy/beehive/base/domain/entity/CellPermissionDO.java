package com.hncboy.beehive.base.domain.entity;

import com.hncboy.beehive.base.enums.CellPermissionTypeEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/6/1
 * cell 权限表实体类
 * 对于 cell 来说必须拥有权限
 */
@Data
@TableName("bh_cell_permission")
public class CellPermissionDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    /**
     * 用户 id
     */
    private Integer userId;

    /**
     * cell code
     */
    private String cellCode;

    /**
     * 权限类型
     */
    private CellPermissionTypeEnum type;

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
