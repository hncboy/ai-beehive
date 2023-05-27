package cn.beehive.cell.bing.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/26
 * Bing 房间消息发送参数
 */
@Data
@Schema(title = "Bing 房间消息发送参数")
public class RoomBingMsgSendRequest {

    @NotNull(message = "房间 id 不能为空")
    @Schema(title = "房间 id")
    private Long roomId;

    // TODO 不同类型限制字数不一样
    @Size(min = 1, max = 3000, message = "消息内容长度必须在 1-3000 之间")
    @NotNull(message = "消息内容不能为空")
    @Schema(title = "消息内容")
    private String content;

    /**
     * h3precise -- 准确模式
     * h3imaginative -- 创造模式
     * harmonyv3 -- 均衡模式
     */
    private String mode;

    /**
     * 换模式了会自动开启
     */
    @Schema(title = "是否开启新话题")
    private Boolean isNewTopic;
}
