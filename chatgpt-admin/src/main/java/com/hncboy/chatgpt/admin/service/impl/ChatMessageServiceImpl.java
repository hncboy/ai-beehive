package com.hncboy.chatgpt.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.admin.mapper.ChatMessageMapper;
import com.hncboy.chatgpt.admin.service.ChatMessageService;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023/3/27 21:46
 * 聊天记录相关业务实现类
 */
@Service("AdminChatMessageServiceImpl")
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessageDO> implements ChatMessageService {

}
