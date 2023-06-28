package com.hncboy.beehive.base.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/18
 * 游标查询参数
 */
@Data
@Valid
public class CursorQuery {

    @Max(value = 100, message = "条数最大为 100")
    @Min(value = 1, message = "条数最小为 1")
    @Schema(title = "条数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "条数不能为空")
    private Integer size;

    /**
     * 第一次查询的话不需要 cursor
     */
    @Schema(title = "是否使用游标 true 是 false 否", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否使用游标")
    private Boolean isUseCursor;

    @Schema(title = "一般是主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "游标不能为空")
    private String cursor;

    @Schema(title = "是否升序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否升序不能为空")
    private Boolean isAsc;
}
