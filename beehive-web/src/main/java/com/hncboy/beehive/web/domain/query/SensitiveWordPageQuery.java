package com.hncboy.beehive.web.domain.query;

import com.hncboy.beehive.base.domain.query.PageQuery;
import com.hncboy.beehive.base.enums.EnableDisableStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
