package com.hncboy.chatgpt.front.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import com.hncboy.chatgpt.base.mapper.ChatRoomMapper;
import com.hncboy.chatgpt.base.util.WebUtil;
import com.hncboy.chatgpt.front.service.ChatRoomService;
import com.hncboy.chatgpt.front.util.FrontUserUtil;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023-3-25
 * 聊天室相关业务实现类
 */
@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomDO> implements ChatRoomService {

    @Override
    public ChatRoomDO createChatRoom(ChatMessageDO chatMessageDO) {
        ChatRoomDO chatRoom = new ChatRoomDO();
        chatRoom.setId(IdWorker.getId());
        chatRoom.setApiType(chatMessageDO.getApiType());
        chatRoom.setIp(WebUtil.getIp());
        chatRoom.setFirstChatMessageId(chatMessageDO.getId());
        chatRoom.setFirstMessageId(chatMessageDO.getMessageId());
        // 取一部分内容当标题，可以通过 GPT 生成标题
        chatRoom.setTitle(StrUtil.sub(chatMessageDO.getContent(), 0, 50));
        chatRoom.setUserId(FrontUserUtil.getUserId());
        save(chatRoom);
        return chatRoom;
    }
}
