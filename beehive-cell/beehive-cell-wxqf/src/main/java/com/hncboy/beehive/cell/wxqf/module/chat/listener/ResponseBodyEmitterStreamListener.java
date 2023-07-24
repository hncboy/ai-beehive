package com.hncboy.beehive.cell.wxqf.module.chat.listener;

import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.base.util.ResponseBodyEmitterUtil;
import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import com.hncboy.beehive.cell.wxqf.module.chat.api.WxqfChatApiCommonResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/7/24
 * ResponseBodyEmitter 消息流监听
 */
@Slf4j
@AllArgsConstructor
public class ResponseBodyEmitterStreamListener extends AbstractStreamListener {

    private final ResponseBodyEmitter emitter;

    @Override
    public void onMessage(WxqfChatApiCommonResponse chatApiCommonResponse, String newMessage, RoomWxqfChatMsgVO roomWxqfChatMsgVO) {
        if (Objects.isNull(roomWxqfChatMsgVO)) {
            return;
        }

        // 发送消息
        ResponseBodyEmitterUtil.send(emitter, roomWxqfChatMsgVO);

        if (chatApiCommonResponse.getIsEnd()) {
            emitter.complete();
        }
    }

    @Override
    public void onError(RoomWxqfChatMsgVO roomWxqfChatMsgVO, Throwable t, @Nullable Response response) {
        if (Objects.isNull(roomWxqfChatMsgVO)) {
            ResponseBodyEmitterUtil.sendWithComplete(emitter, R.fail("【接收消息处理异常，响应中断】"));
            return;
        }
        roomWxqfChatMsgVO.setContent(roomWxqfChatMsgVO.getContent().concat("\n【接收消息处理异常，响应中断】"));
        ResponseBodyEmitterUtil.sendWithComplete(emitter, roomWxqfChatMsgVO);
    }
}
