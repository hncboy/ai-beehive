package com.hncboy.beehive.cell.openai.controller;

import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.core.annotation.CellConfigCheck;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiChatSendRequest;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiChatMsgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/31
 * OpenAi 对话房间控制器
 */
@AllArgsConstructor
@Tag(name = "OpenAi 对话房间相关接口")
@RequestMapping("/room/openai_chat")
@RestController
public class RoomOpenAiChatController {

    private final RoomOpenAiChatMsgService roomOpenAiChatMsgService;

    @Operation(summary = "消息列表")
    @GetMapping("/list")
    public R<List<RoomOpenAiChatMsgVO>> list(@Validated RoomMsgCursorQuery cursorQuery) {
        return R.data(roomOpenAiChatMsgService.list(cursorQuery));
    }

    @CellConfigCheck(roomId = "#sendRequest.roomId")
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public ResponseBodyEmitter send(@Validated @RequestBody RoomOpenAiChatSendRequest sendRequest, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return roomOpenAiChatMsgService.send(sendRequest);
    }
}
