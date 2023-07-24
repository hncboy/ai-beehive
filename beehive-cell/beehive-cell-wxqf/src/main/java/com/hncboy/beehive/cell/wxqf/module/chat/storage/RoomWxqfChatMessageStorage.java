package com.hncboy.beehive.cell.wxqf.module.chat.storage;

import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonResponse;
import com.hncboy.beehive.cell.wxqf.module.chat.parser.ResponseParser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/7/24
 * 对话消息数据存储业业务参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomWxqfChatMessageStorage {

    /**
     * 问题消息
     */
    private RoomWxqfChatMsgDO questionMessageDO;

    /**
     * 回答消息
     */
    private RoomWxqfChatMsgDO answerMessageDO;

    /**
     * 接口响应数据列表
     */
    private LinkedList<WxqfChatApiCommonResponse> apiCommonResponses;

    /**
     * 响应数据解析器
     */
    private ResponseParser<?> parser;

    /**
     * 上一次响应数据
     */
    private WxqfChatApiCommonResponse lastApiCommonResponse;

    /**
     * 异常响应数据
     */
    private String errorResponseData;

    /**
     * 当前已经响应内容
     */
    private String receivedMessage;
}
