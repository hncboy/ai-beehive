package com.hncboy.chatgpt.service.impl;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hncboy.chatgpt.config.ChatConfig;
import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.domain.vo.ChatConfigVO;
import com.hncboy.chatgpt.domain.vo.ChatReplyMessageVO;
import com.hncboy.chatgpt.enums.ApiTypeEnum;
import com.hncboy.chatgpt.exception.ServiceException;
import com.hncboy.chatgpt.handler.ChatThreadHandler;
import com.hncboy.chatgpt.service.ChatService;
import com.plexpt.chatgpt.ChatGPTStream;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;
import com.plexpt.chatgpt.listener.AbstractStreamListener;
import com.plexpt.chatgpt.util.Proxys;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

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

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public ChatConfigVO getChatConfig() {
        ChatConfigVO chatConfigVO = new ChatConfigVO();
        chatConfigVO.setApiModel(chatConfig.getApiTypeEnum());
        chatConfigVO.setBalance(fetchBalance());
        chatConfigVO.setHttpsProxy(chatConfig.getHttpsProxy());
        chatConfigVO.setReverseProxy(chatConfig.getApiReverseProxy());
        chatConfigVO.setSocksProxy(StrUtil.isAllNotEmpty(chatConfig.getSocksProxyHost(), String.valueOf(chatConfig.getSocksProxyPort()))
                ? String.format("%s:%s", chatConfig.getSocksProxyHost(), chatConfig.getSocksProxyPort())
                : StrPool.DASHED);
        chatConfigVO.setTimeoutMs(chatConfig.getTimeoutMs());
        return chatConfigVO;
    }

    @Override
    public ResponseBodyEmitter chatProcess(ChatProcessRequest chatProcessRequest) {
        ApiTypeEnum apiTypeEnum = chatConfig.getApiTypeEnum();
        if (apiTypeEnum == ApiTypeEnum.ACCESS_TOKEN) {
            throw new ServiceException("暂不支持 accessToken");
        }

        return apiKeyResponseEmitter(chatProcessRequest);
    }

    /**
     * ApiKey 的方式
     *
     * @param chatProcessRequest 消息处理请求
     * @return ResponseBodyEmitter
     */
    private ResponseBodyEmitter apiKeyResponseEmitter(ChatProcessRequest chatProcessRequest) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        AbstractStreamListener abstractStreamListener = new AbstractStreamListener() {

            /**
             * 是否是第一条
             */
            private boolean isFirstMessage = true;

            @Override
            public void onMsg(String s) {
                ChatReplyMessageVO chatReplyMessageVO = new ChatReplyMessageVO();
                chatReplyMessageVO.setText(lastMessage);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    String oneMsg = objectMapper.writeValueAsString(chatReplyMessageVO);
                    emitter.send((!isFirstMessage ? "\n" : "") + oneMsg, MediaType.APPLICATION_JSON);
                    isFirstMessage = false;
                } catch (Exception e) {
                    throw new ServiceException("发送消息失败");
                }
            }

            @Override
            public void onError(Throwable throwable, String s) {
                emitter.completeWithError(throwable);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                // 发送完毕
                emitter.complete();
            }
        };

        // 系统消息
        Message systemMessage = Message.ofSystem("You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible.\\nKnowledge cutoff: 2021-09-01\\nCurrent date: 2023-03-23");
        // 用户消息
        Message userMessage = Message.of(chatProcessRequest.getPrompt());

        // 构建对话参数
        ChatCompletion chatCompletion = ChatCompletion.builder()
                .maxTokens(1000)
                .model(chatConfig.getOpenaiApiModel())
                .temperature(0.8)
                .topP(1)
                .presencePenalty(1)
                .messages(Arrays.asList(systemMessage, userMessage))
                .stream(true)
                .build();

        // 国内需要代理，国外不需要
        Proxy proxy = Proxy.NO_PROXY;
        if (chatConfig.hasSocksProxy()) {
            proxy = Proxys.http(chatConfig.getSocksProxyHost(), chatConfig.getSocksProxyPort());
        }

        // 构建请求参数
        ChatGPTStream chatGPTStream = ChatGPTStream.builder()
                .timeout(chatConfig.getTimeoutMs())
                .apiKey(chatConfig.getOpenaiApiKey())
                .proxy(proxy)
                .apiHost(chatConfig.getOpenaiApiBaseUrl())
                .build()
                .init();

        // 创建 Runnable
        Runnable chatListenerTask = () -> chatGPTStream.streamChatCompletion(chatCompletion, abstractStreamListener);
        // 执行消息处理
        ChatThreadHandler.executeChatProcess(chatListenerTask, emitter);
        return emitter;
    }

    /**
     * 拉取余额
     *
     * @return 余额
     */
    @SuppressWarnings("unchecked")
    private String fetchBalance() {
        // ApiKey 才能查询余额
        ApiTypeEnum apiTypeEnum = chatConfig.getApiTypeEnum();
        if (apiTypeEnum != ApiTypeEnum.API_KEY) {
            return StrPool.DASHED;
        }

        // 构建请求
        ForestRequest<?> forestRequest = Forest.get(chatConfig.getOpenaiApiBaseUrl() + "/dashboard/billing/credit_grants")
                .addHeader("Authorization", "Bearer ".concat(chatConfig.getOpenaiApiKey()));

        // 设置正向代理
        if (chatConfig.hasSocksProxy()) {
            ForestProxy proxy = new ForestProxy(chatConfig.getSocksProxyHost(), chatConfig.getSocksProxyPort());
            forestRequest.proxy(proxy);
        }

        // 发起查询余额的请求
        ForestResponse<String> forestResponse = forestRequest.execute(ForestResponse.class);
        if (Objects.isNull(forestResponse) || !forestResponse.isSuccess()) {
            return "拉取余额失败";
        }

        try {
            Map<String, Object> responseMap = objectMapper.readValue(forestResponse.getResult(), new TypeReference<>() {
            });
            return String.valueOf(responseMap.getOrDefault("total_available", "0"));
        } catch (Exception e) {
            log.error("解析余额失败，响应结果：{}", forestResponse.getResult(), e);
            return "解析余额失败";
        }
    }
}
