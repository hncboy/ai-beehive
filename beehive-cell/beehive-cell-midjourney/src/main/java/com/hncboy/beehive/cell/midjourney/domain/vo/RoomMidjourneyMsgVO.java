package com.hncboy.beehive.cell.midjourney.domain.vo;

import com.hncboy.beehive.base.enums.MessageTypeEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.base.enums.MidjourneyMsgActionEnum;
import com.hncboy.beehive.base.handler.serializer.FilePathPrefixSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 房间消息
 */
@Data
@Schema(title = "Midjourney 房间消息")
public class RoomMidjourneyMsgVO {

    @Schema(title = "消息 id")
    private Long id;

    @Schema(title = "房间 id")
    private Long roomId;

    @Schema(title = "消息类型")
    private MessageTypeEnum type;

    @Schema(title = "用户输入")
    private String prompt;

    @Schema(title = "最终的输入")
    private String finalPrompt;

    @Schema(title = "响应内容")
    private String responseContent;

    @Schema(title = "指令动作枚举")
    private MidjourneyMsgActionEnum action;

    @Schema(title = "u 指令使用比特位，末尾 4 位 0000，分别表示 U1 U2 U3 U4")
    private Integer uUseBit;

    @Schema(title = "uv 位置")
    private Integer uvIndex;

    @Schema(title = "状态枚举")
    private MidjourneyMsgStatusEnum status;

    @Schema(title = "discord 开始时间")
    private Date discordStartTime;

    @Schema(title = "discord 结束时间")
    private Date discordFinishTime;

    @Schema(title = "discord 图片地址")
    private String discordImageUrl;

    @Schema(title = "压缩图片地址")
    @JsonSerialize(using = FilePathPrefixSerializer.class)
    private String compressedImageUrl;

    @Schema(title = "原始图片地址")
    @JsonSerialize(using = FilePathPrefixSerializer.class)
    private String originalImageUrl;

    @Schema(title = "排队中的队列长度")
    private Integer waitQueueLength;

    @Schema(title = "创建时间")
    private Date createTime;
}
