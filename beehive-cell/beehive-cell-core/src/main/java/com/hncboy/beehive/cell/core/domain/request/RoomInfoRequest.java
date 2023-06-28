package com.hncboy.beehive.cell.core.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间信息请求参数
 */
@Data
@Schema(title = "房间信息请求参数")
@Valid
public class RoomInfoRequest {

    @Size(min = 1, max = 20, message = "房间名称不能超过 20 个字")
    @NotNull(message = "房间名称不能为空")
    @Schema(title = "房间名称")
    private String name;

    @Schema(title = "房间配色")
    private String color;
}
