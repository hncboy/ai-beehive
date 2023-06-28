package com.hncboy.beehive.base.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023/5/26
 * 房间消息通用查询参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "房间消息通用查询参数")
public class RoomMsgCursorQuery extends CursorQuery {

    @NotNull(message = "房间 id 不能为空")
    @Schema(title = "房间 id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roomId;
}

