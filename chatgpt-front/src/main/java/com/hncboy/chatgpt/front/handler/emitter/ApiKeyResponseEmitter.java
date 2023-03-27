package com.hncboy.chatgpt.front.handler.emitter;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.api.apikey.ApiKeyChatClientBuilder;
import com.hncboy.chatgpt.front.api.listener.ConsoleStreamListener;
import com.hncboy.chatgpt.front.api.listener.ParsedEventSourceListener;
import com.hncboy.chatgpt.front.api.listener.ResponseBodyEmitterStreamListener;
import com.hncboy.chatgpt.front.api.parser.ChatCompletionResponseParser;
import com.hncboy.chatgpt.front.api.storage.ApiKeyDatabaseDataStorage;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.service.ChatMessageService;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/3/24 15:51
 * ApiKey 响应处理
 */
@Component
public class ApiKeyResponseEmitter implements ResponseEmitter {

    @Resource
    private ChatConfig chatConfig;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private ChatCompletionResponseParser parser;

    @Resource
    private ApiKeyDatabaseDataStorage dataStorage;

    @Override
    public ResponseBodyEmitter requestToResponseEmitter(ChatProcessRequest chatProcessRequest) {
        // 初始化聊天消息
        ChatMessageDO chatMessageDO = chatMessageService.initChatMessage(chatProcessRequest, ApiTypeEnum.API_KEY);

        // 所有消息
        LinkedList<Message> messages = new LinkedList<>();
        // 添加用户上下文消息
        addContextQuestionChatMessage(chatMessageDO, messages);

        // 系统角色消息
        if (StrUtil.isNotBlank(chatProcessRequest.getSystemMessage())) {
            // 系统消息
            Message systemMessage = Message.builder()
                    .role(Message.Role.SYSTEM)
                    .content(chatProcessRequest.getSystemMessage())
                    .build();
            messages.addFirst(systemMessage);
        }

        // 构建聊天参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .maxTokens(1000)
                .model(chatConfig.getOpenaiApiModel())
                // [0, 2] 越低越精准
                .temperature(0.8)
                .topP(1.0)
                // 每次生成一条
                .n(1)
                .presencePenalty(1)
                .messages(messages)
                .stream(true)
                .build();

        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setOriginalRequestData(ObjectMapperUtil.toJson(chatCompletion))
                .setChatMessageDO(chatMessageDO)
                .build();

        ApiKeyChatClientBuilder.buildOpenAiStreamClient().streamChatCompletion(chatCompletion, parsedEventSourceListener);
        return emitter;
    }

    /**
     * 添加上下文问题消息
     *
     * @param chatMessageDO 当前消息
     * @param messages      消息列表
     */
    private void addContextQuestionChatMessage(ChatMessageDO chatMessageDO, LinkedList<Message> messages) {
        if (Objects.isNull(chatMessageDO)) {
            return;
        }
        // 从下往上找并添加，越上面的数据放越前面
        messages.addFirst(Message.builder().role(Message.Role.USER)
                .content(chatMessageDO.getContent())
                .build());
        addContextQuestionChatMessage(chatMessageService.getById(chatMessageDO.getParentQuestionMessageId()), messages);
    }
}
