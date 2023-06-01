package cn.beehive.base.domain.entity;

import cn.beehive.base.enums.CellPermissionTypeEnum;
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
 * cell 配置项权限表实体类
 * 对于 cell config 来说，这里的权限是扩展
 * cell config 里的字段本身就携带部分权限
 */
@Data
@TableName(value = "bh_cell_config_permission")
public class CellConfigPermissionDO {

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
     * cell config code
     */
    private String cellConfigCode;

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
