package com.hncboy.chatgpt.api.parser;

import cn.hutool.core.collection.CollectionUtil;
import com.hncboy.chatgpt.util.ObjectMapperUtil;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/24 17:47
 * ApiKey 的 ChatCompletion 响应解析器
 */
@Component
public class ChatCompletionResponseParser implements ResponseParser<ChatCompletionResponse> {

    @Override
    public ChatCompletionResponse parseSuccess(String originalData) {
        return ObjectMapperUtil.fromJson(originalData, ChatCompletionResponse.class);
    }

    @Override
    public String parseContent(String originalData) {
        ChatCompletionResponse response = parseSuccess(originalData);
        List<ChatChoice> choices = response.getChoices();
        if (CollectionUtil.isEmpty(choices)) {
            return null;
        }
        Message delta = choices.get(0).getDelta();
        return delta.getContent();
    }
}
