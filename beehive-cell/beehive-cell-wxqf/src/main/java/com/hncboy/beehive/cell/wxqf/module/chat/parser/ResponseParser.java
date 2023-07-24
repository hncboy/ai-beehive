package com.hncboy.beehive.cell.wxqf.module.chat.parser;

import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import com.hncboy.beehive.cell.wxqf.module.chat.storage.RoomWxqfChatMessageStorage;

/**
 * @author hncboy
 * @date 2023/7/24
 * 响应数据解析器接口
 */
public interface ResponseParser<SUCCESS> {

    /**
     * 解析响应成功的原始数据
     *
     * @param originalData 原始数据
     * @return 实体类
     */
    SUCCESS parseSuccess(String originalData);

    /**
     * 解析接收到消息转成当前收到的所有消息
     *
     * @param receivedMessage 已经接收到的所有消息
     * @param newMessage      新的消息
     * @return 当前收到的所有消息
     */
    String parseReceivedMessage(String receivedMessage, String newMessage);

    /**
     * 解析本次返回新消息
     *
     * @param originalData 原始数据
     * @return 消息内容
     */
    String parseNewMessage(String originalData);

    /**
     * 解析错误消息
     *
     * @param originalResponseErrorMsg 原始错误消息
     * @return 错误消息
     */
    String parseErrorMessage(String originalResponseErrorMsg);

    /**
     * 解析回答消息
     *
     * @param chatMessageStorage 消息存储
     * @return 展示内容
     */
    RoomWxqfChatMsgVO parseChatMessageVO(RoomWxqfChatMessageStorage chatMessageStorage);
}
