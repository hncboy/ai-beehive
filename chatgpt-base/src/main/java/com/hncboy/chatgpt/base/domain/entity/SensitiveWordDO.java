package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hncboy.chatgpt.base.enums.EnableDisableStatusEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023-3-28
 * 敏感词表实体类
 */
@Data
@TableName("sensitive_word")
public class SensitiveWordDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 敏感词内容
     */
    private String word;

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

    /**
     * 状态 1 启用 2 停用
     */
    private EnableDisableStatusEnum status;

    /**
     * 是否删除 0 否 NULL 是
     */
    @TableLogic(value = "0", delval = "NULL")
    private Integer isDeleted;
}
