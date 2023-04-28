package com.hncboy.chatgpt.admin.domain.query;

import com.hncboy.chatgpt.base.domain.query.PageQuery;
import com.hncboy.chatgpt.base.enums.EnableDisableStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Size;

/**
 * @author hncboy
 * @date 2023-3-28
 * 敏感词分页查询
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "敏感词分页查询")
public class SensitiveWordPageQuery extends PageQuery {

    @Size(max = 20, message = "敏感词内容不超过 20 个字")
    @Schema(title = "敏感词内容")
    private String word;

    @Schema(title = "状态 1 启用 2 停用")
    private EnableDisableStatusEnum status;
}
