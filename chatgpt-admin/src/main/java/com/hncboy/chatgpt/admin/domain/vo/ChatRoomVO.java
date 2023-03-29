package com.hncboy.chatgpt.admin.domain.vo;

import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/3/27 23:21
 * 聊天室展示参数
 */
@Data
@Schema(name = "聊天室展示参数")
public class ChatRoomVO {

    @Schema(name = "聊天室 id")
    private Long id;

    @Schema(name = "对话 id", description = "唯一")
    private String conversationId;

    @Schema(name = "ip")
    private String ip;

    @Schema(name = "第一条消息 id", description = "唯一")
    private String firstMessageId;

    @Schema(name = "对话标题，从第一条消息截取")
    private String title;

    @Schema(name = "API 类型", description = "不同类型的对话不能一起存储")
    private ApiTypeEnum apiType;

    @Schema(name = "创建时间")
    private Date createTime;

    @Schema(name = "更新时间")
    private Date updateTime;
}
