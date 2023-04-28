package com.hncboy.chatgpt.admin.handler.converter;

import com.hncboy.chatgpt.admin.domain.vo.ChatRoomVO;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023-3-27
 * 聊天室相关转换
 */
@Mapper
public interface ChatRoomConverter {

    ChatRoomConverter INSTANCE = Mappers.getMapper(ChatRoomConverter.class);

    /**
     * entityToVO
     *
     * @param chatRoomDOList chatRoomDOList
     * @return List<ChatRoomVO>
     */
    List<ChatRoomVO> entityToVO(List<ChatRoomDO> chatRoomDOList);
}
