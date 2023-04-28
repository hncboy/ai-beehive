package com.hncboy.chatgpt.front.handler.emitter;

import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import com.hncboy.chatgpt.base.enums.ConversationModelEnum;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.api.accesstoken.AccessTokenApiClient;
import com.hncboy.chatgpt.front.api.accesstoken.ConversationRequest;
import com.hncboy.chatgpt.front.api.listener.ParsedEventSourceListener;
import com.hncboy.chatgpt.front.api.listener.ResponseBodyEmitterStreamListener;
import com.hncboy.chatgpt.front.api.parser.AccessTokenChatResponseParser;
import com.hncboy.chatgpt.front.api.storage.AccessTokenDatabaseDataStorage;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.service.ChatMessageService;
import com.unfbx.chatgpt.entity.chat.Message;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Collections;
import java.util.UUID;

/**
 * @author hncboy
 * @date 2023-3-24
 * AccessToken 响应处理
 */
@Component
public class AccessTokenResponseEmitter implements ResponseEmitter {

    @Resource
    private ChatConfig chatConfig;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private AccessTokenChatResponseParser parser;

    @Resource
    private AccessTokenDatabaseDataStorage dataStorage;

    @Override
    public void requestToResponseEmitter(ChatProcessRequest chatProcessRequest, ResponseBodyEmitter emitter) {
        // 构建 accessTokenApiClient
        AccessTokenApiClient accessTokenApiClient = AccessTokenApiClient.builder()
                .accessToken(chatConfig.getOpenaiAccessToken())
                .reverseProxy(chatConfig.getApiReverseProxy())
                .model(chatConfig.getOpenaiApiModel())
                .build();

        // 初始化聊天消息
        ChatMessageDO chatMessageDO = chatMessageService.initChatMessage(chatProcessRequest, ApiTypeEnum.ACCESS_TOKEN);

        // 构建 ConversationRequest
        ConversationRequest conversationRequest = buildConversationRequest(chatMessageDO);

        // 构建事件监听器
        ParsedEventSourceListener parsedEventSourceListener = new ParsedEventSourceListener.Builder()
//                .addListener(new ConsoleStreamListener())
                .addListener(new ResponseBodyEmitterStreamListener(emitter))
                .setParser(parser)
                .setDataStorage(dataStorage)
                .setOriginalRequestData(ObjectMapperUtil.toJson(conversationRequest))
                .setChatMessageDO(chatMessageDO)
                .build();

        // 发送请求
        accessTokenApiClient.streamChatCompletions(conversationRequest, parsedEventSourceListener);
    }

    /**
     * 构建 ConversationRequest
     *
     * @param chatMessageDO 聊天消息
     * @return ConversationRequest
     */
    private ConversationRequest buildConversationRequest(ChatMessageDO chatMessageDO) {
        // 构建 content
        ConversationRequest.Content content = ConversationRequest.Content.builder()
                .parts(Collections.singletonList(chatMessageDO.getContent()))
                .build();

        // 构建 Message
        ConversationRequest.Message message = ConversationRequest.Message.builder()
                .id(chatMessageDO.getMessageId())
                .role(Message.Role.USER.getName())
                .content(content)
                .build();

        // 构建 ConversationRequest
        String model = chatConfig.getOpenaiApiModel();

        return ConversationRequest.builder()
                .messages(Collections.singletonList(message))
                .action(ConversationRequest.MessageActionTypeEnum.NEXT)
                .model(ConversationModelEnum.NAME_MAP.get(model))
                // 父级消息 id 不能为空，不然会报错，因此第一条消息也需要随机生成一个
                .parentMessageId(StrUtil.isBlank(chatMessageDO.getParentMessageId()) ? UUID.randomUUID().toString() : chatMessageDO.getParentMessageId())
                .conversationId(chatMessageDO.getConversationId())
                .build();
    }
}
