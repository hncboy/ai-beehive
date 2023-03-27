package com.hncboy.chatgpt.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.admin.mapper.ChatRoomMapper;
import com.hncboy.chatgpt.admin.service.ChatRoomService;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023/3/27 21:46
 * 聊天室相关业务实现类
 */
@Service("AdminChatRoomServiceImpl")
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomDO> implements ChatRoomService {

}
