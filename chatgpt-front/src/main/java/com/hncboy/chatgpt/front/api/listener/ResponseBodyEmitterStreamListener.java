package com.hncboy.chatgpt.front.api.listener;

import com.hncboy.chatgpt.base.exception.ServiceException;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.domain.vo.ChatReplyMessageVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-24
 * ResponseBodyEmitter 消息流监听
 */
@Slf4j
@AllArgsConstructor
public class ResponseBodyEmitterStreamListener extends AbstractStreamListener {

    private final ResponseBodyEmitter emitter;

    @Override
    public void onMessage(String newMessage, String receivedMessage, ChatReplyMessageVO chatReplyMessageVO, int messageCount) {
        if (Objects.isNull(chatReplyMessageVO)) {
            return;
        }

        try {
            emitter.send((messageCount != 1 ? "\n" : "") + ObjectMapperUtil.toJson(chatReplyMessageVO));
        } catch (Exception e) {
            log.warn("消息发送异常，第{}条消息，消息内容：{}", messageCount, receivedMessage, e);
            throw new ServiceException("消息发送异常");
        }
    }

    @Override
    public void onComplete(String receivedMessage) {
        emitter.complete();
    }

    @Override
    public void onError(String receivedMessage, Throwable t, @Nullable Response response) {
        try {
            ChatReplyMessageVO chatReplyMessageVO = new ChatReplyMessageVO();
            chatReplyMessageVO.setText(receivedMessage.concat("\n【接收消息处理异常，响应中断】"));
            emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
        } catch (Exception e) {
            log.warn("消息发送异常，处理异常发送消息时出错", e);
        } finally {
            try {
                emitter.complete();
            } catch (Exception ignored) {

            }
        }
    }
}
