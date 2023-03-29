package com.hncboy.chatgpt.front.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import com.hncboy.chatgpt.base.util.WebUtil;
import com.hncboy.chatgpt.front.mapper.ChatRoomMapper;
import com.hncboy.chatgpt.front.service.ChatRoomService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/3/25 16:31
 * 聊天室相关业务实现类
 */
@Service("FrontChatRoomServiceImpl")
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomDO> implements ChatRoomService {

    @Override
    public ChatRoomDO createChatRoom(ChatMessageDO chatMessageDO) {
        ChatRoomDO chatRoom = new ChatRoomDO();
        chatRoom.setId(IdWorker.getId());
        chatRoom.setApiType(chatMessageDO.getApiType());
        chatRoom.setIp(WebUtil.getIp());
        chatRoom.setFirstMessageId(chatMessageDO.getMessageId());
        // 取第一条对话内容当标题
        chatRoom.setTitle(chatMessageDO.getContent());
        chatRoom.setCreateTime(new Date());
        chatRoom.setUpdateTime(new Date());
        save(chatRoom);
        return chatRoom;
    }
}
