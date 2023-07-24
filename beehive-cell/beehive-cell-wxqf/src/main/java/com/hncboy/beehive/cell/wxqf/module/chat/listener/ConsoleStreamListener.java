package com.hncboy.beehive.cell.wxqf.module.chat.listener;

import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonResponse;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

/**
 * @author hncboy
 * @date 2023/7/24
 * 控制台消息流监听
 */
public class ConsoleStreamListener extends AbstractStreamListener {

    @Override
    public void onMessage(WxqfChatApiCommonResponse chatApiCommonResponse, String newMessage, RoomWxqfChatMsgVO roomOpenAiChatMsgVO) {
        System.out.println(newMessage);
    }

    @Override
    public void onError(RoomWxqfChatMsgVO roomOpenAiChatMsgVO, Throwable t, @Nullable Response response) {
        System.out.println("控制台消息输出异常了");
    }
}
