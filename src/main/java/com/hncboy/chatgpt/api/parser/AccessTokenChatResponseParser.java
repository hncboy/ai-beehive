package com.hncboy.chatgpt.api.parser;

import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/3/24 17:53
 * AccessToken 的聊天对话解析器
 */
@Component
public class AccessTokenChatResponseParser implements ResponseParser<String> {

    @Override
    public String parseSuccess(String originalData) {
        return null;
    }

    @Override
    public String parseContent(String originalData) {
        return null;
    }
}
