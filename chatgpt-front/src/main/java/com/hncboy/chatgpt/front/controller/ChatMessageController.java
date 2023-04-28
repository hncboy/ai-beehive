package com.hncboy.chatgpt.front.controller;

import cn.hutool.core.date.DateUtil;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * @author hncboy
 * @date 2023-3-22
 * 聊天相关接口
 */
@AllArgsConstructor
@Tag(name = "聊天相关接口")
@RestController("FrontChatMessageController")
@RequestMapping("/chat_message")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public ResponseBodyEmitter sendMessage(@RequestBody @Validated ChatProcessRequest chatProcessRequest, HttpServletResponse response) {
        // TODO 后续调整
        chatProcessRequest.setSystemMessage("You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible.\\nKnowledge cutoff: 2021-09-01\\nCurrent date: ".concat(DateUtil.today()));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return chatMessageService.sendMessage(chatProcessRequest);
    }
}
