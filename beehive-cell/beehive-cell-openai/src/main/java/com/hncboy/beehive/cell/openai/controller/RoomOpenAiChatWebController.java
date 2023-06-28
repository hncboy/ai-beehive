package com.hncboy.beehive.cell.openai.controller;

import com.hncboy.beehive.base.domain.query.RoomMsgCursorQuery;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import com.hncboy.beehive.cell.openai.service.RoomOpenAiChatWebMsgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/1
 * OpenAi 对话 Web 房间控制器
 */
@AllArgsConstructor
@Tag(name = "OpenAi 对话 Web 房间相关接口")
@RequestMapping("/room/openai_chat_web")
@RestController
public class RoomOpenAiChatWebController {

    private final RoomOpenAiChatWebMsgService roomOpenAiChatWebMsgService;

    @Operation(summary = "消息列表")
    @GetMapping("/list")
    public R<List<RoomOpenAiChatMsgVO>> list(@Validated RoomMsgCursorQuery cursorQuery) {
        return R.data(roomOpenAiChatWebMsgService.list(cursorQuery));
    }
}
