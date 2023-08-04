package com.hncboy.beehive.cell.chatglm.controller;

import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.chatglm.service.RoomChatGlmMsgService;
import com.hncboy.beehive.cell.core.annotation.CellConfigCheck;
import com.hncboy.beehive.cell.chatglm.domain.request.RoomChatGlmSendRequest;
import com.hncboy.beehive.cell.chatglm.domain.vo.RoomChatGlmChatMsgVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

/**
 * @author hanpeng
 * @date 2023/8/3
 * ChatGLM 对话房间控制器
 */
@AllArgsConstructor
@Tag(name = "ChatGLM 对话房间相关接口")
@RequestMapping("/room/chatglm")
@RestController
public class RoomChatGlmChatController {

    private final RoomChatGlmMsgService roomChatGLMMsgService;

    @Operation(summary = "消息列表")
    @GetMapping("/list")
    public R<List<RoomChatGlmChatMsgVO>> list(@Validated RoomMsgCursorQuery cursorQuery) {
        return R.data(roomChatGLMMsgService.list(cursorQuery));
    }

    @CellConfigCheck(roomId = "#sendRequest.roomId")
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public ResponseBodyEmitter send(@Validated @RequestBody RoomChatGlmSendRequest sendRequest, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return roomChatGLMMsgService.send(sendRequest);
    }
}
