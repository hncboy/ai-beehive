package com.hncboy.chatgpt.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jankin Wu
 * @TableName front_user_api_key_rel
 */
@TableName(value = "front_user_api_key_rel")
@Builder
@Data
public class FrontUserApiKeyRelDO implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户端用户ID
     */
    private Integer userId;

    /**
     * ApiKey
     */
    private String apiKey;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}