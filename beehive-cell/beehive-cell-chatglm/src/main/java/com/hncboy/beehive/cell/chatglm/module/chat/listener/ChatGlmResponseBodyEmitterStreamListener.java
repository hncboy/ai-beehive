package com.hncboy.beehive.cell.chatglm.module.chat.listener;

import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.base.util.ResponseBodyEmitterUtil;
import com.hncboy.beehive.cell.chatglm.domain.vo.RoomChatGlmChatMsgVO;
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
public class ChatGlmResponseBodyEmitterStreamListener{

    private final ResponseBodyEmitter emitter;

    /**
     * 初始化
     */
    public void onInit() {

    }

    public void onMessage(String newMessage, RoomChatGlmChatMsgVO roomChatGlmMsgVO) {
        if (Objects.isNull(roomChatGlmMsgVO)) {
            return;
        }

        // 发送消息
        ResponseBodyEmitterUtil.send(emitter, roomChatGlmMsgVO);
    }

    public void onComplete(String receivedMessage) {
        emitter.complete();
    }

    public void onError(RoomChatGlmChatMsgVO roomChatGlmMsgVO, Throwable t, @Nullable Response response) {
        if (Objects.isNull(roomChatGlmMsgVO)) {
            ResponseBodyEmitterUtil.sendWithComplete(emitter, R.fail("【接收消息处理异常，响应中断】"));
            return;
        }
        roomChatGlmMsgVO.setContent(roomChatGlmMsgVO.getContent().concat("\n【接收消息处理异常，响应中断】"));
        ResponseBodyEmitterUtil.sendWithComplete(emitter, roomChatGlmMsgVO);
    }
}
