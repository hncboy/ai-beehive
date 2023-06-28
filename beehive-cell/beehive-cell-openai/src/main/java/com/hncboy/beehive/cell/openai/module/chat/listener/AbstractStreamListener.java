package com.hncboy.beehive.cell.openai.module.chat.listener;

import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

/**
 * @author hncboy
 * @date 2023-3-24
 * 消息流监听器抽象类
 */
public abstract class AbstractStreamListener {

    /**
     * 初始化
     */
    public void onInit() {

    }

    /**
     * 接收到新消息
     *
     * @param newMessage          新的单条消息
     * @param roomOpenAiChatMsgVO 消息展示参数
     */
    public abstract void onMessage(String newMessage, RoomOpenAiChatMsgVO roomOpenAiChatMsgVO);

    /**
     * 结束响应
     *
     * @param receivedMessage 接收到消息
     */
    public void onComplete(String receivedMessage) {

    }

    /**
     * 异常处理
     *
     * @param roomOpenAiChatMsgVO 消息展示参数
     * @param t                   异常
     * @param response            响应信息
     */
    public abstract void onError(RoomOpenAiChatMsgVO roomOpenAiChatMsgVO, Throwable t, @Nullable Response response);
}
