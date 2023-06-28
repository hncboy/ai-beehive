package com.hncboy.beehive.cell.bing.controller;

import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.bing.domain.request.RoomBingMsgSendRequest;
import com.hncboy.beehive.cell.bing.domain.vo.RoomBingMsgVO;
import com.hncboy.beehive.cell.bing.service.RoomBingMsgService;
import com.hncboy.beehive.cell.core.annotation.CellConfigCheck;
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
 * @date 2023/5/26
 * NewBing 房间控制器
 */
@AllArgsConstructor
@Tag(name = "NewBing 房间相关接口")
@RequestMapping("/room/bing")
@RestController
public class RoomBingController {

    private final RoomBingMsgService roomBingMsgService;

    @Operation(summary = "消息列表")
    @GetMapping("/list")
    public R<List<RoomBingMsgVO>> list(@Validated RoomMsgCursorQuery cursorQuery) {
        return R.data(roomBingMsgService.list(cursorQuery));
    }

    @CellConfigCheck(roomId = "#sendRequest.roomId")
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public ResponseBodyEmitter send(@Validated @RequestBody RoomBingMsgSendRequest sendRequest, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return roomBingMsgService.send(sendRequest);
    }
}
