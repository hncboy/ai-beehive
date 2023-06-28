package com.hncboy.beehive.cell.openai.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/31
 * OpenAi 对话房间消息发送参数
 */
@Data
@Schema(title = "OpenAi 对话消息发送参数")
public class RoomOpenAiChatSendRequest {

    @NotNull(message = "房间 id 不能为空")
    @Schema(title = "房间 id")
    private Long roomId;

    @Size(min = 1, max = 5000, message = "消息内容长度必须在 1-5000 之间")
    @NotNull(message = "消息内容不能为空")
    @Schema(title = "消息内容")
    private String content;
}
