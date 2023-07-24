package com.hncboy.beehive.cell.wxqf.controller;

import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.core.annotation.CellConfigCheck;
import com.hncboy.beehive.cell.wxqf.domain.request.RoomWxqfChatSendRequest;
import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import com.hncboy.beehive.cell.wxqf.service.RoomWxqfChatMsgService;
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
 * @date 2023/7/24
 * 文心千帆对话房间控制器
 */
@AllArgsConstructor
@Tag(name = "文心千帆对话房间相关接口")
@RequestMapping("/room/wxqf_chat")
@RestController
public class RoomWxqfChatController {

    private final RoomWxqfChatMsgService roomWxqfChatMsgService;

    @Operation(summary = "消息列表")
    @GetMapping("/list")
    public R<List<RoomWxqfChatMsgVO>> list(@Validated RoomMsgCursorQuery cursorQuery) {
        return R.data(roomWxqfChatMsgService.list(cursorQuery));
    }

    @CellConfigCheck(roomId = "#sendRequest.roomId")
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public ResponseBodyEmitter send(@Validated @RequestBody RoomWxqfChatSendRequest sendRequest, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return roomWxqfChatMsgService.send(sendRequest);
    }
}
