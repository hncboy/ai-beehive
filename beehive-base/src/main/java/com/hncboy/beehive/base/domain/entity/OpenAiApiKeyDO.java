package com.hncboy.beehive.base.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hncboy.beehive.base.enums.OpenAiApiKeyStatusEnum;
import com.hncboy.beehive.base.enums.OpenAiApiKeyUseSceneEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/30
 * OpenAi ApkKey 表实体类
 */
@TableName("bh_openai_api_key")
@Data
public class OpenAiApiKeyDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * apiKey
     */
    private String apiKey;

    /**
     * 请求地址
     */
    private String baseUrl;

    /**
     * 使用场景列表
     */
    private List<OpenAiApiKeyUseSceneEnum> useScenes;

    /**
     * 总额度（美元）
     */
    private BigDecimal totalBalance;

    /**
     * 已使用额度（美元）
     */
    private BigDecimal usageBalance;

    /**
     * 剩余额度（美元）
     */
    private BigDecimal remainBalance;

    /**
     * 余额水位线（美元）
     */
    private BigDecimal balanceWaterLine;

    /**
     * 刷新状态时间
     * 刷新余额伴随着刷新状态
     */
    private Date refreshStatusTime;

    /**
     * 刷新余额时间
     */
    private Date refreshBalanceTime;

    /**
     * 是否刷新余额
     * 余额刷新不一定准确，所以某些 apiKey 可以不刷新
     */
    private Boolean isRefreshBalance;

    /**
     * 是否刷新状态
     * 用于检测账号是否封禁
     */
    private Boolean isRefreshStatus;

    /**
     * 权重，权重高的优先执行
     */
    private Integer weight;

    /**
     * 状态
     */
    private OpenAiApiKeyStatusEnum status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新理由
     */
    private String updateReason;

    /**
     * 错误信息
     */
    private String errorInfo;

    /**
     * 版本
     */
    private Integer version;

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
