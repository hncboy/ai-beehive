package com.hncboy.beehive.cell.midjourney.controller;

import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.core.annotation.CellConfigCheck;
import com.hncboy.beehive.cell.midjourney.domain.request.MjConvertRequest;
import com.hncboy.beehive.cell.midjourney.domain.request.MjDescribeRequest;
import com.hncboy.beehive.cell.midjourney.domain.request.MjImagineRequest;
import com.hncboy.beehive.cell.midjourney.domain.vo.RoomMidjourneyMsgVO;
import com.hncboy.beehive.cell.midjourney.service.RoomMidjourneyMsgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 房间控制器
 */
@AllArgsConstructor
@Tag(name = "Midjourney 房间相关接口")
@RequestMapping("/room/midjourney")
@RestController
public class RoomMidjourneyController {

    private final RoomMidjourneyMsgService roomMidjourneyMsgService;

    @Operation(summary = "消息列表")
    @GetMapping("/list")
    public R<List<RoomMidjourneyMsgVO>> list(@Validated RoomMsgCursorQuery cursorQuery) {
        return R.data(roomMidjourneyMsgService.list(cursorQuery));
    }

    @Operation(summary = "消息详情")
    @GetMapping("/detail")
    public R<RoomMidjourneyMsgVO> detail(@RequestParam @Parameter(description = "消息 id") Long msgId) {
        return R.data(roomMidjourneyMsgService.detail(msgId));
    }

    @CellConfigCheck(roomId = "#imagineRequest.roomId")
    @Operation(summary = "imagine")
    @PostMapping("/imagine")
    public R<Boolean> imagine(@Validated @RequestBody MjImagineRequest imagineRequest) {
        roomMidjourneyMsgService.imagine(imagineRequest);
        return R.data(true);
    }

    @CellConfigCheck(roomId = "#convertRequest.roomId")
    @Operation(summary = "upscale")
    @PostMapping("/upscale")
    public R<Boolean> upscale(@Validated @RequestBody MjConvertRequest convertRequest) {
        roomMidjourneyMsgService.upscale(convertRequest);
        return R.data(true);
    }

    @CellConfigCheck(roomId = "#convertRequest.roomId")
    @Operation(summary = "variation")
    @PostMapping("/variation")
    public R<Boolean> variation(@Validated @RequestBody MjConvertRequest convertRequest) {
        roomMidjourneyMsgService.variation(convertRequest);
        return R.data(true);
    }

    @CellConfigCheck(roomId = "#describeRequest.roomId")
    @Operation(summary = "describe")
    @PostMapping("/describe")
    public R<Boolean> describe(@Validated @ModelAttribute MjDescribeRequest describeRequest) {
        roomMidjourneyMsgService.describe(describeRequest);
        return R.data(true);
    }
}
