package com.hncboy.chatgpt.handler.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author hncboy
 * @date 2023/3/25 17:54
 * 聊天记录相关转换
 */
@Mapper
public interface ChatMessageConverter {

    ChatMessageConverter INSTANCE = Mappers.getMapper(ChatMessageConverter.class);
}
