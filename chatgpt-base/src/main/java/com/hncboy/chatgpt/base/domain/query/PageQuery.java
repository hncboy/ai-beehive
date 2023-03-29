package com.hncboy.chatgpt.base.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author hncboy
 * @date 2023/3/27 23:14
 * 分页参数
 */
@Data
@Valid
public class PageQuery {

    @Schema(title = "第几页", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "第几页不能为空")
    private Integer pageSize;

    @Schema(title = "每页多少条", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "每页条数不能为空")
    private Integer pageNum;
}

