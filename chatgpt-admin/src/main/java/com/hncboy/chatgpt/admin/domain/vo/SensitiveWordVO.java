package com.hncboy.chatgpt.admin.domain.vo;

import com.hncboy.chatgpt.base.enums.EnableDisableStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023-3-28
 * 敏感词展示参数
 */
@Data
@Schema(title = "敏感词展示参数")
public class SensitiveWordVO {

    @Schema(title = "主键")
    private Long id;

    @Schema(title = "敏感词内容")
    private String word;

    @Schema(title = "状态 1 启用 2 停用")
    private EnableDisableStatusEnum status;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;
}
