package com.hncboy.beehive.cell.openai.controller;

import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.core.annotation.CellConfigCheck;
import com.hncboy.beehive.cell.openai.domain.request.RoomOpenAiImageSendRequest;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiImageMsgVO;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiImageMsgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/3
 * OpenAi 图像房间控制器
 */
@AllArgsConstructor
@Tag(name = "OpenAi 图像房间相关接口")
@RequestMapping("/room/openai_image")
@RestController
public class RoomOpenAiImageController {

    private final RoomOpenAiImageMsgService roomOpenAiImageMsgService;

    @Operation(summary = "消息列表")
    @GetMapping("/list")
    public R<List<RoomOpenAiImageMsgVO>> list(@Validated RoomMsgCursorQuery cursorQuery) {
        return R.data(roomOpenAiImageMsgService.list(cursorQuery));
    }

    @CellConfigCheck(roomId = "#sendRequest.roomId")
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public R<RoomOpenAiImageMsgVO> send(@Validated @RequestBody RoomOpenAiImageSendRequest sendRequest) {
        return R.data(roomOpenAiImageMsgService.send(sendRequest));
    }
}
