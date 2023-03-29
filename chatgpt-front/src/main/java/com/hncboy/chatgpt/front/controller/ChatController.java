package com.hncboy.chatgpt.front.controller;

import com.hncboy.chatgpt.base.annotation.FrontPreAuth;
import com.hncboy.chatgpt.base.handler.response.R;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.domain.vo.ChatConfigVO;
import com.hncboy.chatgpt.front.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hncboy
 * @date 2023/3/22 19:47
 * 聊天相关接口
 */
@FrontPreAuth
@AllArgsConstructor
@Tag(name = "聊天相关接口")
@RestController
@RequestMapping
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "配置信息")
    @PostMapping("/config")
    public R<ChatConfigVO> chatConfig() {
        return R.data(chatService.getChatConfig());
    }

    @Operation(summary = "消息处理")
    @PostMapping("/chat-process")
    public ResponseBodyEmitter chatProcess(@RequestBody @Validated ChatProcessRequest chatProcessRequest, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return chatService.chatProcess(chatProcessRequest);
    }
}
