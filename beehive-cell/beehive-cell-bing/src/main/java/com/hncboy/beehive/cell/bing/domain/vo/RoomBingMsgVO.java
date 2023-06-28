package com.hncboy.beehive.cell.bing.domain.vo;

import com.hncboy.beehive.base.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * Bing 房间消息展示参数
 */
@Data
@Schema(title = "Bing 房间消息展示参数")
public class RoomBingMsgVO {

    @Schema(title = "主键")
    private Long id;

    @Schema(title = "消息内容")
    private String content;

    @Schema(title = "最大提问次数")
    private Integer maxNumUserMessagesInConversation;

    @Schema(title = "累计提问次数")
    private Integer numUserMessagesInConversation;

    @Schema(title = "建议")
    private List<String> suggestResponses;

    @Schema(title = "消息类型")
    private MessageTypeEnum type;

    @Schema(title = "创建时间")
    private Date createTime;
}
