package com.hncboy.chatgpt.service.impl;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hncboy.chatgpt.api.ChatClientUtil;
import com.hncboy.chatgpt.config.ChatConfig;
import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.domain.vo.ChatConfigVO;
import com.hncboy.chatgpt.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;

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
        chatConfigVO.setBalance(String.valueOf(ChatClientUtil.buildOpenAiClient().creditGrants().getTotalAvailable()));
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
        return SpringUtil.getBean(chatConfig.getApiTypeEnum().getResponseEmitterClazz()).requestToResponseEmitter(chatProcessRequest);
    }
}
