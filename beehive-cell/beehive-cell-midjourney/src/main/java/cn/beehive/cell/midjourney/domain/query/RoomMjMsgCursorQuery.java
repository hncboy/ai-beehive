package cn.beehive.cell.midjourney.domain.query;

import cn.beehive.base.domain.query.CursorQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 房间消息查询参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "Midjourney 房间消息查询参数")
public class RoomMjMsgCursorQuery extends CursorQuery {

    @NotNull(message = "房间 id 不能为空")
    @Schema(title = "房间 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roomId;
}
