package com.hncboy.chatgpt.admin.domain.vo;

import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import com.hncboy.chatgpt.base.enums.ChatMessageStatusEnum;
import com.hncboy.chatgpt.base.enums.ChatMessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023-3-28
 * 聊天记录展示参数
 */
@Data
@Schema(title = "聊天记录展示参数")
public class ChatMessageVO {

    @Schema(title = "消息 id")
    private String messageId;

    @Schema(title = "父级消息 id", description = "第一条消息父级消息 id 为空；回答的父级消息 id 不能为空")
    private String parentMessageId;

    @Schema(title = "父级回答消息 id", description = "当前消息是问题：则 parentMessageId=parentAnswerMessageId")
    private String parentAnswerMessageId;

    @Schema(title = "父级问题消息 id", description = "当前消息是回答：则 parentMessageId=parentQuestionMessageId")
    private String parentQuestionMessageId;

    @Schema(title = "上下文数量", description = "包含了问题和回答的数量；第一条消息是 1")
    private Integer contextCount;

    @Schema(title = "问题上下文数量", description = "包含了连续的问题的数量；第一条消息是 1")
    private Integer questionContextCount;

    @Schema(title = "消息类型枚举", description = "第一条消息一定是问题")
    private ChatMessageTypeEnum messageType;

    @Schema(title = "聊天室 id")
    private Long chatRoomId;

    @Schema(title = "对话 id")
    private String conversationId;

    @Schema(title = "API 类型")
    private ApiTypeEnum apiType;

    @Schema(title = "ip")
    private String ip;

    @Schema(title = "消息内容", description = "包含上下文的对话这里只会显示出用户发送的")
    private String content;

    @Schema(title = "错误响应数据")
    private String responseErrorData;

    @Schema(title = "输入消息的 tokens")
    private Long promptTokens;

    @Schema(title = "输出消息的 tokens")
    private Long completionTokens;

    @Schema(title = "累计 Tokens")
    private Long totalTokens;

    @Schema(title = "聊天信息状态")
    private ChatMessageStatusEnum status;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;
}
