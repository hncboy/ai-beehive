package com.hncboy.chatgpt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.enums.ApiTypeEnum;

/**
 * @author hncboy
 * @date 2023/3/25 16:30
 * 聊天记录相关业务接口
 */
public interface ChatMessageService extends IService<ChatMessageDO> {

    /**
     * 初始化聊天消息
     *
     * @param chatProcessRequest 消息处理请求参数
     * @param apiTypeEnum        API 类型
     * @return 聊天消息
     */
    ChatMessageDO initChatMessage(ChatProcessRequest chatProcessRequest, ApiTypeEnum apiTypeEnum);
}
