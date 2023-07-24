package com.hncboy.beehive.cell.wxqf.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话房间消息发送参数
 */
@Data
@Schema(title = "文心千帆对话消息发送参数")
public class RoomWxqfChatSendRequest {

    @NotNull(message = "房间 id 不能为空")
    @Schema(title = "房间 id")
    private Long roomId;

    @Size(min = 1, max = 1000, message = "消息内容长度必须在 1-1000 之间")
    @NotNull(message = "消息内容不能为空")
    @Schema(title = "消息内容")
    private String content;
}
