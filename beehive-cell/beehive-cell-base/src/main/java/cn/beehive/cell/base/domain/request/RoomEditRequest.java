package cn.beehive.cell.base.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023/5/29
 * 编辑房间请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "编辑房间请求参数")
public class RoomEditRequest extends RoomInfoRequest {

    @NotNull(message = "房间 id 不能为空")
    @Schema(title = "房间 id")
    private Long roomId;
}
