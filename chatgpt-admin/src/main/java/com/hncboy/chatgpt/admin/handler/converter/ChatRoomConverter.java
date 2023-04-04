package com.hncboy.chatgpt.admin.handler.converter;

import cn.hutool.core.util.DesensitizedUtil;
import com.hncboy.chatgpt.admin.domain.vo.ChatRoomVO;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/27 23:25
 * 聊天室相关转换
 */
@Mapper
public interface ChatRoomConverter {

    ChatRoomConverter INSTANCE = Mappers.getMapper(ChatRoomConverter.class);

    List<ChatRoomVO> entityToVO(List<ChatRoomDO> chatRoomDOList);

    @AfterMapping
    default void afterEntityToVO(ChatRoomDO chatRoomDO, @MappingTarget ChatRoomVO chatRoomVO) {
        chatRoomVO.setTitle("新聊天室");
        chatRoomVO.setIp(DesensitizedUtil.ipv4(chatRoomDO.getIp()));
    }
}
