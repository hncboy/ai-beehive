package com.hncboy.beehive.cell.bing.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * Bing 房间流式消息
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(title = "Bing 房间流式消息")
public class RoomBingStreamMsgVO {

    @Schema(title = "内容")
    private String content;

    @Schema(title = "最大提问次数")
    private Integer maxNumUserMessagesInConversation;

    @Schema(title = "累计提问次数")
    private Integer numUserMessagesInConversation;

    @Schema(title = "建议")
    private List<String> suggests;
}
