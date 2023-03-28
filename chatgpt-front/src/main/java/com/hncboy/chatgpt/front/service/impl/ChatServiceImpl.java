package com.hncboy.chatgpt.front.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.chatgpt.base.config.ChatConfig;
import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import com.hncboy.chatgpt.base.exception.ServiceException;
import com.hncboy.chatgpt.base.handler.SensitiveWordHandler;
import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.api.apikey.ApiKeyChatClientBuilder;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.domain.vo.ChatConfigVO;
import com.hncboy.chatgpt.front.handler.emitter.AccessTokenResponseEmitter;
import com.hncboy.chatgpt.front.handler.emitter.ApiKeyResponseEmitter;
import com.hncboy.chatgpt.front.handler.emitter.ResponseEmitter;
import com.hncboy.chatgpt.front.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/22 19:41
 * 聊天相关业务实现类
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatConfig chatConfig;

    @Override
    public ChatConfigVO getChatConfig() {
        ChatConfigVO chatConfigVO = new ChatConfigVO();
        chatConfigVO.setApiModel(chatConfig.getApiTypeEnum());
        chatConfigVO.setBalance(String.valueOf(ApiKeyChatClientBuilder.buildOpenAiClient().creditGrants().getTotalAvailable()));
        chatConfigVO.setHttpsProxy(StrUtil.isAllNotEmpty(chatConfig.getHttpProxyHost(), String.valueOf(chatConfig.getHttpProxyPort()))
                ? String.format("%s:%s", chatConfig.getHttpProxyHost(), chatConfig.getHttpProxyPort())
                : StrPool.DASHED);
        chatConfigVO.setReverseProxy(chatConfig.getApiReverseProxy());
        chatConfigVO.setSocksProxy(StrPool.DASHED);
        chatConfigVO.setTimeoutMs(chatConfig.getTimeoutMs());
        return chatConfigVO;
    }

    @Override
    public ResponseBodyEmitter chatProcess(ChatProcessRequest chatProcessRequest) {
        List<String> prompts = SensitiveWordHandler.checkWord(chatProcessRequest.getPrompt());
        if (CollectionUtil.isNotEmpty(prompts)) {
            throw new ServiceException(StrUtil.format("发送失败，包含敏感词{}", prompts));
        }
        List<String> systemMessages = SensitiveWordHandler.checkWord(chatProcessRequest.getSystemMessage());
        if (CollectionUtil.isNotEmpty(systemMessages)) {
            throw new ServiceException(StrUtil.format("发送失败，系统消息包含敏感词{}", prompts));
        }

        ApiTypeEnum apiTypeEnum = chatConfig.getApiTypeEnum();
        ResponseEmitter responseEmitter;
        if (apiTypeEnum == ApiTypeEnum.API_KEY) {
            responseEmitter = SpringUtil.getBean(ApiKeyResponseEmitter.class);
        } else {
            responseEmitter = SpringUtil.getBean(AccessTokenResponseEmitter.class);
        }

        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));
        emitter.onTimeout(() -> log.error("请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));
        return responseEmitter.requestToResponseEmitter(chatProcessRequest, emitter);
    }
}
