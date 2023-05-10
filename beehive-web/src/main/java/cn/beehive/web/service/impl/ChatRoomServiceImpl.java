package cn.beehive.web.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.beehive.base.domain.entity.ChatMessageDO;
import cn.beehive.base.domain.entity.ChatRoomDO;
import cn.beehive.base.mapper.ChatRoomMapper;
import cn.beehive.base.util.WebUtil;
import cn.beehive.web.service.ChatRoomService;
import cn.beehive.web.util.FrontUserUtil;
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
