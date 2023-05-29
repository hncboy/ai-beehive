package cn.beehive.base.domain.entity;

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
@TableName(value = "bh_cell_config")
@Data
public class CellConfigDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * cell id
     */
    private Integer cellId;

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
     */
    private Boolean isRequired;

    /**
     * 用户是否可见，false 否 true 是
     */
    private Boolean isUserVisible;

    /**
     * 有些配置用户可修改但是看不到默认值
     * 用户是否可见 value，false 否 true 是
     */
    private Boolean isUserValueVisible;

    /**
     * 有些配置用户可见但是不能修改
     * 用户是否可修改，false 否 true 是
     */
    private Boolean isUserModifiable;

    /**
     * 用户创建房间后是否可修改，false 否 true 是
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