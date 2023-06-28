package com.hncboy.beehive.cell.openai.module.chat.listener;

import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

/**
 * @author hncboy
 * @date 2023-3-24
 * 控制台消息流监听
 */
public class ConsoleStreamListener extends AbstractStreamListener {

    @Override
    public void onMessage(String newMessage, RoomOpenAiChatMsgVO roomOpenAiChatMsgVO) {
        System.out.println(newMessage);
    }

    @Override
    public void onError(RoomOpenAiChatMsgVO roomOpenAiChatMsgVO, Throwable t, @Nullable Response response) {
        System.out.println("控制台消息输出异常了");
    }
}
