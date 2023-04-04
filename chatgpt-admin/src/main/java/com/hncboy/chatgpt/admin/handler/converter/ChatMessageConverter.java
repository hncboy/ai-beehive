package com.hncboy.chatgpt.admin.handler.converter;

import cn.hutool.core.util.DesensitizedUtil;
import com.hncboy.chatgpt.admin.domain.vo.ChatMessageVO;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.enums.ChatMessageTypeEnum;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/28 12:39
 * 聊天记录相关转换
 */
@Mapper
public interface ChatMessageConverter {

    ChatMessageConverter INSTANCE = Mappers.getMapper(ChatMessageConverter.class);

    List<ChatMessageVO> entityToVO(List<ChatMessageDO> chatMessageDOList);

    @AfterMapping
    default void afterEntityToVO(ChatMessageDO chatMessageDO, @MappingTarget ChatMessageVO chatMessageVO) {
        if (chatMessageDO.getMessageType() == ChatMessageTypeEnum.ANSWER) {
            chatMessageVO.setContent("回了一条消息");
        } else if (chatMessageDO.getMessageType() == ChatMessageTypeEnum.QUESTION) {
            chatMessageVO.setContent("问了一条消息");
        }
        chatMessageVO.setIp(DesensitizedUtil.ipv4(chatMessageDO.getIp()));
    }
}
