package com.hncboy.beehive.cell.chatglm.module.chat.storage;

import com.hncboy.beehive.cell.chatglm.module.chat.parser.ChatGlmCompletionResponseParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hncboy
 * @date 2023-3-25
 * ChatGLM 对话消息数据存储业业务参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomChatGlmMessageStorage {

    /**
     * 问题消息
     */
    private Object questionMessageDO;

    /**
     * 回答消息
     */
    private Object answerMessageDO;

    /**
     * 原始响应数据
     */
    private String originalResponseData;

    /**
     * 异常响应数据
     */
    private String errorResponseData;

    /**
     * 响应解析器
     */
    private ChatGlmCompletionResponseParser parser;

    /**
     * 当前已经响应内容
     */
    private String receivedMessage;

    /**
     * 当前消息流条数
     */
    private Integer currentStreamMessageCount;
}
