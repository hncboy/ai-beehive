package com.hncboy.beehive.cell.openai.handler.converter;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatMsgDO;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/31
 * OpenAi 对话房间消息相关转换
 */
@Mapper
public interface RoomOpenAiChatMsgConverter {

    RoomOpenAiChatMsgConverter INSTANCE = Mappers.getMapper(RoomOpenAiChatMsgConverter.class);

    /**
     * List<RoomOpenAiChatMsgDO> 转 List<RoomOpenAiChatMsgVO>
     *
     * @param roomOpenAiChatMsgDOList List<RoomOpenAiChatMsgDO>
     * @return List<RoomOpenAiChatMsgVO>
     */
    List<RoomOpenAiChatMsgVO> entityToVO(List<RoomOpenAiChatMsgDO> roomOpenAiChatMsgDOList);
}
