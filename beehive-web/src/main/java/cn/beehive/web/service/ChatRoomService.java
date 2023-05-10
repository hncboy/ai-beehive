package cn.beehive.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.beehive.base.domain.entity.ChatMessageDO;
import cn.beehive.base.domain.entity.ChatRoomDO;

/**
 * @author hncboy
 * @date 2023-3-25
 * 聊天室相关业务接口
 */
public interface ChatRoomService extends IService<ChatRoomDO> {

    /**
     * 创建聊天室
     *
     * @param chatMessageDO 聊天记录
     * @return 聊天室
     */
    ChatRoomDO createChatRoom(ChatMessageDO chatMessageDO);
}
