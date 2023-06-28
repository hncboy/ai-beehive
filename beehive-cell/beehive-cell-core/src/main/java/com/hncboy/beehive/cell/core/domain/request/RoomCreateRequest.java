package com.hncboy.beehive.cell.core.domain.request;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间创建请求参数
 */
@Data
@Schema(title = "房间创建请求参数")
public class RoomCreateRequest {

    @NotNull(message = "cell code 不能为空")
    @Schema(title = "cell code")
    private CellCodeEnum cellCode;

    @NotNull(message = "房间信息不能为空")
    @Schema(title = "房间信息")
    private RoomInfoRequest roomInfo;

    @NotNull(message = "房间配置参数列表不能为空")
    @Schema(title = "房间配置参数列表")
    private List<RoomConfigParamRequest> roomConfigParams;
}
