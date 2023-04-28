package com.hncboy.chatgpt.front.handler.emitter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.handler.SensitiveWordHandler;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.domain.vo.ChatReplyMessageVO;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.List;

/**
 * @author hncboy
 * @date 2023-3-29
 * 敏感词检测
 */
public class SensitiveWordEmitterChain extends AbstractResponseEmitterChain {

    @Override
    public void doChain(ChatProcessRequest request, ResponseBodyEmitter emitter) {
        List<String> prompts = SensitiveWordHandler.checkWord(request.getPrompt());
        List<String> systemMessages = SensitiveWordHandler.checkWord(request.getSystemMessage());
        try {
            // 取上一条消息的 parentMessageId 和 conversationId，使得敏感词检测未通过时不影响上下文
            ChatReplyMessageVO chatReplyMessageVO = ChatReplyMessageVO.onEmitterChainException(request);
            if (CollectionUtil.isNotEmpty(prompts)) {
                chatReplyMessageVO.setText(StrUtil.format("发送失败，包含敏感词{}", prompts));
                emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
                emitter.complete();
                return;
            }

            if (CollectionUtil.isNotEmpty(systemMessages)) {
                chatReplyMessageVO.setText(StrUtil.format("发送失败，系统消息包含敏感词{}", prompts));
                emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
                emitter.complete();
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (getNext() != null) {
            getNext().doChain(request, emitter);
        }
    }
}
