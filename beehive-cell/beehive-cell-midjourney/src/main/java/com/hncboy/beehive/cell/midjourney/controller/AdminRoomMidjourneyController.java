package com.hncboy.beehive.cell.midjourney.controller;

import com.hncboy.beehive.base.annotation.ApiAdminRestController;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.midjourney.service.AdminRoomMidjourneyMsgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hncboy
 * @date 2023/6/30
 * Midjourney 房间控制器
 */
@AllArgsConstructor
@Tag(name = "管理端-Midjourney 房间相关接口")
@ApiAdminRestController("/room/midjourney")
public class AdminRoomMidjourneyController {

    private final AdminRoomMidjourneyMsgService adminRoomMidjourneyMsgService;

    @Operation(summary = "标记异常消息")
    @GetMapping("/mark_error_message")
    public R<Void> markErrorMessage(@RequestParam Long msgId) {
        // 给一个接口直接更新状态，如果自己看到 discord 报错可以直接掉接口更新，不用等到定时任务执行
        adminRoomMidjourneyMsgService.markErrorMessage(msgId);
        return R.success("成功");
    }
}
