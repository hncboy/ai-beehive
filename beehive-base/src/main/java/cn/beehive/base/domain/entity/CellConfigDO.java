package cn.beehive.base.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName cell_config
 */
@TableName(value ="cell_config")
@Data
public class CellConfigDO implements Serializable {
    /**
     * 代理主键
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * cell表ID
     */
    @TableField(value = "cell_id")
    private Integer cellId;

    /**
     * 配置项名称
     */
    @TableField(value = "key_name")
    private String keyName;

    /**
     * 该配置项的默认值
     */
    @TableField(value = "default_value")
    private String defaultValue;

    /**
     * 是否必填，0非必填，1必填
     */
    @TableField(value = "is_required")
    private Boolean isRequired;

    /**
     * 用户是否可见，0不可见，1可见
     */
    @TableField(value = "is_user_visible")
    private Boolean isUserVisible;

    /**
     * 用户是否可修改，0不可以，1可以
     */
    @TableField(value = "is_user_modifiable")
    private Boolean isUserModifiable;

    /**
     * 用户是否在创建后可修改，0不可以，1可以
     */
    @TableField(value = "is_user_live_modifiable")
    private Boolean isUserLiveModifiable;

    /**
     * 示例值
     */
    @TableField(value = "example_value")
    private String exampleValue;

    /**
     * 配置项介绍
     */
    @TableField(value = "introduce")
    private String introduce;

    /**
     * 备注，管理端查看
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    /**
     * 配置项MD5，检测是否发生更新
     */
    @TableField(value = "md5")
    private String md5;

    /**
     * 是否删除，0未删除，1已删除
     */
    @TableField(value = "is_delete")
    private Boolean isDelete;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CellConfigDO other = (CellConfigDO) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCellId() == null ? other.getCellId() == null : this.getCellId().equals(other.getCellId()))
            && (this.getKeyName() == null ? other.getKeyName() == null : this.getKeyName().equals(other.getKeyName()))
            && (this.getDefaultValue() == null ? other.getDefaultValue() == null : this.getDefaultValue().equals(other.getDefaultValue()))
            && (this.getIsRequired() == null ? other.getIsRequired() == null : this.getIsRequired().equals(other.getIsRequired()))
            && (this.getIsUserVisible() == null ? other.getIsUserVisible() == null : this.getIsUserVisible().equals(other.getIsUserVisible()))
            && (this.getIsUserModifiable() == null ? other.getIsUserModifiable() == null : this.getIsUserModifiable().equals(other.getIsUserModifiable()))
            && (this.getIsUserLiveModifiable() == null ? other.getIsUserLiveModifiable() == null : this.getIsUserLiveModifiable().equals(other.getIsUserLiveModifiable()))
            && (this.getExampleValue() == null ? other.getExampleValue() == null : this.getExampleValue().equals(other.getExampleValue()))
            && (this.getIntroduce() == null ? other.getIntroduce() == null : this.getIntroduce().equals(other.getIntroduce()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getMd5() == null ? other.getMd5() == null : this.getMd5().equals(other.getMd5()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCellId() == null) ? 0 : getCellId().hashCode());
        result = prime * result + ((getKeyName() == null) ? 0 : getKeyName().hashCode());
        result = prime * result + ((getDefaultValue() == null) ? 0 : getDefaultValue().hashCode());
        result = prime * result + ((getIsRequired() == null) ? 0 : getIsRequired().hashCode());
        result = prime * result + ((getIsUserVisible() == null) ? 0 : getIsUserVisible().hashCode());
        result = prime * result + ((getIsUserModifiable() == null) ? 0 : getIsUserModifiable().hashCode());
        result = prime * result + ((getIsUserLiveModifiable() == null) ? 0 : getIsUserLiveModifiable().hashCode());
        result = prime * result + ((getExampleValue() == null) ? 0 : getExampleValue().hashCode());
        result = prime * result + ((getIntroduce() == null) ? 0 : getIntroduce().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getMd5() == null) ? 0 : getMd5().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", cellId=").append(cellId);
        sb.append(", keyName=").append(keyName);
        sb.append(", defaultValue=").append(defaultValue);
        sb.append(", isRequired=").append(isRequired);
        sb.append(", isUserVisible=").append(isUserVisible);
        sb.append(", isUserModifiable=").append(isUserModifiable);
        sb.append(", isUserLiveModifiable=").append(isUserLiveModifiable);
        sb.append(", exampleValue=").append(exampleValue);
        sb.append(", introduce=").append(introduce);
        sb.append(", remark=").append(remark);
        sb.append(", version=").append(version);
        sb.append(", md5=").append(md5);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}