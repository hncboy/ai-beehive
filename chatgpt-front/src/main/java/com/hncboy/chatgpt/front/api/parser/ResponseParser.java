package com.hncboy.chatgpt.front.api.parser;

/**
 * @author hncboy
 * @date 2023-3-24
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
     * 1.ApiKey 模式需要拼接前面的句子
     * 2.AccessToken 模式不需要拼接
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
}
