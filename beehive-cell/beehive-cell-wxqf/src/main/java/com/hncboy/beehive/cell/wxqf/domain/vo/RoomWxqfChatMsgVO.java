package com.hncboy.beehive.cell.wxqf.domain.vo;

import com.hncboy.beehive.base.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话房间消息展示参数
 */
@Data
@Schema(title = "文心千帆对话房间消息展示参数")
public class RoomWxqfChatMsgVO {

    @Schema(title = "主键")
    private Long id;

    @Schema(title = "消息内容")
    private String content;

    @Schema(title = "消息类型")
    private MessageTypeEnum messageType;

    @Schema(title = "创建时间")
    private Date createTime;
}
