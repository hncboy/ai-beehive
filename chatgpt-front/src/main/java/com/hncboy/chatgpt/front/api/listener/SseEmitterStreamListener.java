package com.hncboy.chatgpt.front.api.listener;

import com.hncboy.chatgpt.front.domain.vo.ChatReplyMessageVO;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author hncboy
 * @date 2023-3-24
 * SseEmitterStreamListener 消息流监听
 */
@RequiredArgsConstructor
public class SseEmitterStreamListener extends AbstractStreamListener {

    private final SseEmitter emitter;

    @Override
    public void onMessage(String newMessage, String receivedMessage, ChatReplyMessageVO chatReplyMessageVO, int i) {
        // TODO
    }

    @Override
    public void onError(String receivedMessage, Throwable t, @Nullable Response response) {

    }
}
