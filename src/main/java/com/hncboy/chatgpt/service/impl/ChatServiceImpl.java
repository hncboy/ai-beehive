package com.hncboy.chatgpt.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.StringResource;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hncboy.chatgpt.config.ChatConfig;
import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.domain.vo.ChatConfigVO;
import com.hncboy.chatgpt.domain.vo.ChatReplyMessageVO;
import com.hncboy.chatgpt.enums.ApiTypeEnum;
import com.hncboy.chatgpt.exception.ServiceException;
import com.hncboy.chatgpt.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
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
    public InputStream chatProcess(ChatProcessRequest chatProcessRequest) {
        StringBuilder sb = new StringBuilder();
        // TODO 补充接口调用
        ChatReplyMessageVO chatReplyMessageVO1 = new ChatReplyMessageVO();
        chatReplyMessageVO1.setRole("assistant");
        chatReplyMessageVO1.setId("111");
        chatReplyMessageVO1.setParentMessageId("111");
        chatReplyMessageVO1.setConversationId("111");
        chatReplyMessageVO1.setText("Hello");

        ChatReplyMessageVO chatReplyMessageVO2 = new ChatReplyMessageVO();
        chatReplyMessageVO2.setRole("assistant");
        chatReplyMessageVO2.setId("111");
        chatReplyMessageVO2.setParentMessageId("111");
        chatReplyMessageVO2.setConversationId("111");
        chatReplyMessageVO2.setText("Hello World");

        ChatReplyMessageVO chatReplyMessageVO3 = new ChatReplyMessageVO();
        chatReplyMessageVO3.setRole("assistant");
        chatReplyMessageVO3.setId("111");
        chatReplyMessageVO3.setParentMessageId("111");
        chatReplyMessageVO3.setConversationId("111");
        chatReplyMessageVO3.setText("Hello World Here");

        try {
            sb.append(objectMapper.writeValueAsString(chatReplyMessageVO1));
            sb.append("\n").append(objectMapper.writeValueAsString(chatReplyMessageVO2));
            sb.append("\n").append(objectMapper.writeValueAsString(chatReplyMessageVO3));
        } catch (JsonProcessingException e) {
            throw new ServiceException("消息回复出错");
        }

        InputStream inputStream = new StringResource(sb.toString()).getStream();
        // 关闭 inputStream
        IoUtil.close(inputStream);
        return inputStream;
    }

    /**
     * 拉取余额
     *
     * @return 余额
     */
    @SuppressWarnings("unchecked")
    private String fetchBalance() {
        ApiTypeEnum apiTypeEnum = chatConfig.getApiTypeEnum();
        if (apiTypeEnum != ApiTypeEnum.API_KEY) {
            return StrPool.DASHED;
        }

        // 发起查询余额的请求
        ForestResponse<String> forestResponse = Forest.get("https://api.openai.com/dashboard/billing/credit_grants")
                .addHeader("Authorization", "Bearer ".concat(chatConfig.getOpenaiApiKey()))
                .execute(ForestResponse.class);
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
