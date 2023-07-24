package com.hncboy.beehive.cell.wxqf.module.chat.listener;

import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonResponse;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

/**
 * @author hncboy
 * @date 2023/7/24
 * 消息流监听器抽象类
 */
public abstract class AbstractStreamListener {

    /**
     * 接收到新消息
     *
     * @param chatApiCommonResponse 通用响应内容
     * @param newMessage            新的单条消息
     * @param roomOpenAiChatMsgVO   消息展示参数
     */
    public abstract void onMessage(WxqfChatApiCommonResponse chatApiCommonResponse, String newMessage, RoomWxqfChatMsgVO roomOpenAiChatMsgVO);

    /**
     * 异常处理
     *
     * @param roomOpenAiChatMsgVO 消息展示参数
     * @param t                   异常
     * @param response            响应信息
     */
    public abstract void onError(RoomWxqfChatMsgVO roomOpenAiChatMsgVO, Throwable t, @Nullable Response response);
}
