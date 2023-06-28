package com.hncboy.beehive.cell.core.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间配置参数请求参数
 */
@Data
@Schema(title = "房间配置参数请求参数")
public class RoomConfigParamRequest {

    @NotNull(message = "配置项 code 不能为空")
    @Schema(title = "配置项 code")
    private String cellConfigCode;

    @Schema(title = "配置项值，可以为空")
    private String value;

    @NotNull(message = "是否使用默认值不能为空")
    @Schema(title = "是否使用默认值")
    private Boolean isUseDefaultValue;
}
