package com.hncboy.beehive.cell.openai.module.chat.listener;

import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.base.util.ResponseBodyEmitterUtil;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
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
    public void onMessage(String newMessage, RoomOpenAiChatMsgVO roomOpenAiChatMsgVO) {
        if (Objects.isNull(roomOpenAiChatMsgVO)) {
            return;
        }

        // 发送消息
        ResponseBodyEmitterUtil.send(emitter, roomOpenAiChatMsgVO);
    }

    @Override
    public void onComplete(String receivedMessage) {
        emitter.complete();
    }

    @Override
    public void onError(RoomOpenAiChatMsgVO roomOpenAiChatMsgVO, Throwable t, @Nullable Response response) {
        if (Objects.isNull(roomOpenAiChatMsgVO)) {
            ResponseBodyEmitterUtil.sendWithComplete(emitter, R.fail("【接收消息处理异常，响应中断】"));
            return;
        }
        roomOpenAiChatMsgVO.setContent(roomOpenAiChatMsgVO.getContent().concat("\n【接收消息处理异常，响应中断】"));
        ResponseBodyEmitterUtil.sendWithComplete(emitter, roomOpenAiChatMsgVO);
    }
}
