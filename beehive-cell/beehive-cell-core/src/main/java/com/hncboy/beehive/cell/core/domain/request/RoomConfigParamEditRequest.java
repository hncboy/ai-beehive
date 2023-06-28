package com.hncboy.beehive.cell.core.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/3
 * 房间配置参数编辑请求参数
 */
@Data
@Schema(title = "房间配置参数编辑请求参数")
public class RoomConfigParamEditRequest {

    @NotNull(message = "房间 id 不能为空")
    @Schema(title = "房间 id")
    private Long roomId;

    @NotNull(message = "房间配置参数列表不能为空")
    @Schema(title = "房间配置参数列表")
    private List<RoomConfigParamRequest> roomConfigParams;
}
