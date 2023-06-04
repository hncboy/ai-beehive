package cn.beehive.cell.midjourney.domain.vo;

import cn.beehive.base.enums.MessageTypeEnum;
import cn.beehive.base.enums.MjMsgActionEnum;
import cn.beehive.base.enums.MidjourneyMsgStatusEnum;
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

    /**
     * 主键
     */
    private Long id;

    /**
     * 房间 id
     */
    private Long roomId;

    /**
     * 消息类型
     */
    private MessageTypeEnum type;

    /**
     * 用户输入
     */
    private String prompt;

    /**
     * 最终的输入
     */
    private String finalPrompt;

    /**
     * 响应内容
     */
    private String responseContent;

    /**
     * 指令动作
     */
    private MjMsgActionEnum action;

    /**
     * u 指令使用比特位
     * 末尾 4 位 0000
     * 分别表示 U1 U2 U3 U4
     */
    private Integer uUseBit;

    /**
     * uv 位置
     */
    private Integer uvIndex;

    /**
     * 状态
     */
    private MidjourneyMsgStatusEnum status;

    /**
     * discord 开始时间
     */
    private Date discordStartTime;

    /**
     * discord 结束时间
     */
    private Date discordFinishTime;

    /**
     * discord 图片地址
     */
    private String discordImageUrl;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 排队中的队列长度
     */
    private Integer waitQueueLength;

    /**
     * 创建时间
     */
    private Date createTime;
}
