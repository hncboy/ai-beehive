package com.hncboy.chatgpt.front.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import com.hncboy.chatgpt.base.enums.ChatMessageStatusEnum;
import com.hncboy.chatgpt.base.enums.ChatMessageTypeEnum;
import com.hncboy.chatgpt.base.exception.ServiceException;
import com.hncboy.chatgpt.base.mapper.ChatMessageMapper;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.base.util.WebUtil;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.handler.emitter.ChatMessageEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.IpRateLimiterEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.ResponseEmitterChain;
import com.hncboy.chatgpt.front.handler.emitter.SensitiveWordEmitterChain;
import com.hncboy.chatgpt.front.service.ChatMessageService;
import com.hncboy.chatgpt.front.service.ChatRoomService;
import com.hncboy.chatgpt.front.util.FrontUserUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author hncboy
 * @date 2023/3/25 16:33
 * 聊天记录相关业务实现类
 */
@Slf4j
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessageDO> implements ChatMessageService {

    @Resource
    private ChatConfig chatConfig;

    @Resource
    private ChatRoomService chatRoomService;

    @Override
    public ResponseBodyEmitter sendMessage(ChatProcessRequest chatProcessRequest) {
        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));
        emitter.onTimeout(() -> log.error("请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));

        // 构建 emitter 处理链路
        ResponseEmitterChain ipRateLimiterEmitterChain = new IpRateLimiterEmitterChain();
        ResponseEmitterChain sensitiveWordEmitterChain = new SensitiveWordEmitterChain();
        sensitiveWordEmitterChain.setNext(new ChatMessageEmitterChain());
        ipRateLimiterEmitterChain.setNext(sensitiveWordEmitterChain);
        ipRateLimiterEmitterChain.doChain(chatProcessRequest, emitter);
        return emitter;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ChatMessageDO initChatMessage(ChatProcessRequest chatProcessRequest, ApiTypeEnum apiTypeEnum) {
        ChatMessageDO chatMessageDO = new ChatMessageDO();
        chatMessageDO.setId(IdWorker.getId());
        // 消息 id 手动生成
        chatMessageDO.setMessageId(UUID.randomUUID().toString());
        chatMessageDO.setMessageType(ChatMessageTypeEnum.QUESTION);
        chatMessageDO.setApiType(apiTypeEnum);
        if (apiTypeEnum == ApiTypeEnum.API_KEY) {
            chatMessageDO.setApiKey(chatConfig.getOpenaiApiKey());
        }
        chatMessageDO.setUserId(FrontUserUtil.getUserId());
        chatMessageDO.setContent(chatProcessRequest.getPrompt());
        chatMessageDO.setModelName(chatConfig.getOpenaiApiModel());
        chatMessageDO.setOriginalData(null);
        chatMessageDO.setPromptTokens(-1);
        chatMessageDO.setCompletionTokens(-1);
        chatMessageDO.setTotalTokens(-1);
        chatMessageDO.setIp(WebUtil.getIp());
        chatMessageDO.setStatus(ChatMessageStatusEnum.INIT);
        chatMessageDO.setCreateTime(new Date());
        chatMessageDO.setUpdateTime(new Date());

        // 填充初始化父级消息参数
        populateInitParentMessage(chatMessageDO, chatProcessRequest);

        save(chatMessageDO);
        return chatMessageDO;
    }

    /**
     * 填充初始化父级消息参数
     *
     * @param chatMessageDO      消息记录
     * @param chatProcessRequest 消息处理请求参数
     */
    private void populateInitParentMessage(ChatMessageDO chatMessageDO, ChatProcessRequest chatProcessRequest) {
        // 父级消息 id
        String parentMessageId = Optional.ofNullable(chatProcessRequest.getOptions())
                .map(ChatProcessRequest.Options::getParentMessageId)
                .orElse(null);

        // 对话 id
        String conversationId = Optional.ofNullable(chatProcessRequest.getOptions())
                .map(ChatProcessRequest.Options::getConversationId)
                .orElse(null);

        if (StrUtil.isAllNotBlank(parentMessageId, conversationId)) {
            // 寻找父级消息
            ChatMessageDO parentChatMessage = getOne(new LambdaQueryWrapper<ChatMessageDO>()
                    // 用户 id 一致
                    .eq(ChatMessageDO::getUserId, FrontUserUtil.getUserId())
                    // 消息 id 一致
                    .eq(ChatMessageDO::getMessageId, parentMessageId)
                    // 对话 id 一致
                    .eq(ChatMessageDO::getConversationId, conversationId)
                    // Api 类型一致
                    .eq(ChatMessageDO::getApiType, chatMessageDO.getApiType())
                    // 消息类型为回答
                    .eq(ChatMessageDO::getMessageType, ChatMessageTypeEnum.ANSWER));
            if (Objects.isNull(parentChatMessage)) {
                throw new ServiceException("父级消息不存在，本次对话出错，请先关闭上下文或开启新的对话窗口");
            }

            chatMessageDO.setParentMessageId(parentMessageId);
            chatMessageDO.setParentAnswerMessageId(parentMessageId);
            chatMessageDO.setParentQuestionMessageId(parentChatMessage.getParentQuestionMessageId());
            chatMessageDO.setChatRoomId(parentChatMessage.getChatRoomId());
            chatMessageDO.setConversationId(parentChatMessage.getConversationId());
            chatMessageDO.setContextCount(parentChatMessage.getContextCount() + 1);
            chatMessageDO.setQuestionContextCount(parentChatMessage.getQuestionContextCount() + 1);

            if (chatMessageDO.getApiType() == ApiTypeEnum.ACCESS_TOKEN) {
                if (!Objects.equals(chatMessageDO.getModelName(), parentChatMessage.getModelName())) {
                    throw new ServiceException(StrUtil.format("当前对话类型为 AccessToken 使用模型不一样，请开启新的对话"));
                }
            }

            // ApiKey 限制上下文问题的数量
            if (chatMessageDO.getApiType() == ApiTypeEnum.API_KEY
                    && chatConfig.getLimitQuestionContextCount() > 0
                    && chatMessageDO.getQuestionContextCount() > chatConfig.getLimitQuestionContextCount()) {
                throw new ServiceException(StrUtil.format("当前允许连续对话的问题数量为[{}]次，已达到上限，请关闭上下文对话重新发送", chatConfig.getLimitQuestionContextCount()));
            }
        } else {
            // 创建新聊天室
            ChatRoomDO chatRoomDO = chatRoomService.createChatRoom(chatMessageDO);
            chatMessageDO.setChatRoomId(chatRoomDO.getId());
            chatMessageDO.setContextCount(1);
            chatMessageDO.setQuestionContextCount(1);
        }
    }
}
