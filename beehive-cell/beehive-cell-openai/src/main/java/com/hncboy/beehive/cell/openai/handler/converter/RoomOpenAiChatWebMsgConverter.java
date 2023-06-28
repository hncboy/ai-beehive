package com.hncboy.beehive.cell.openai.handler.converter;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiChatWebMsgDO;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiChatMsgVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/1
 * OpenAi 对话 Web 房间消息相关转换
 */
@Mapper
public interface RoomOpenAiChatWebMsgConverter {

    RoomOpenAiChatWebMsgConverter INSTANCE = Mappers.getMapper(RoomOpenAiChatWebMsgConverter.class);

    /**
     * List<RoomOpenAiChatWebMsgDO> 转 List<RoomOpenAiChatMsgVO>
     *
     * @param roomOpenAiChatWebMsgDOList List<RoomOpenAiChatWebMsgDO>
     * @return List<RoomOpenAiChatMsgVO>
     */
    List<RoomOpenAiChatMsgVO> entityToVO(List<RoomOpenAiChatWebMsgDO> roomOpenAiChatWebMsgDOList);
}
