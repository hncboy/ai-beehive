package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 为实现轮流给用户分配key，记录已使用的key
 *
 * @author Jankin Wu
 * @TableName current_key
 */
@TableName(value = "current_key")
@Builder
@Data
public class CurrentKeyDO implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 已给用户分配的key
     */
    private String currentKey;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}