package com.hncboy.chatgpt.api.listener;

import com.hncboy.chatgpt.domain.vo.ChatReplyMessageVO;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author hncboy
 * @date 2023/3/24 16:21
 * 消息流监听器抽象类
 */
public abstract class AbstractStreamListener {

    /**
     * 接收到新消息
     *
     * @param newMessage         新的单条消息
     * @param receivedMessage    已经接收到的所有消息，包含当前新消息
     * @param chatReplyMessageVO ChatReplyMessageVO
     * @param messageCount       消息条数
     */
    public abstract void onMessage(String newMessage, String receivedMessage, ChatReplyMessageVO chatReplyMessageVO, int messageCount);

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
     * @param receivedMessage 接收到消息
     * @param t               异常
     * @param response        响应信息
     */
    public abstract void onError(String receivedMessage, Throwable t, @Nullable Response response);

    /**
     * 所有消息接收完毕的时候
     */
    @Setter
    @Getter
    protected Consumer<String> onComplete = s -> {
        // s 是完整的消息
    };
}
