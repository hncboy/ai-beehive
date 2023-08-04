package com.hncboy.beehive.cell.chatglm.handler.converter;

import com.hncboy.beehive.base.domain.entity.RoomChatGlmMsgDO;
import com.hncboy.beehive.cell.chatglm.domain.vo.RoomChatGlmChatMsgVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hanpeng
 * @date 2023/8/4
 * ChatGLM 对话房间消息相关转换
 */
@Mapper
public interface RoomChatGlmMsgConverter {

    RoomChatGlmMsgConverter INSTANCE = Mappers.getMapper(RoomChatGlmMsgConverter.class);

    /**
     * List<RoomChatGLMMsgDO> 转 List<RoomChatGLMChatMsgVO>
     *
     * @param roomChatGlmMsgDOList List<RoomChatGlmMsgDO>
     * @return List<RoomChatGLMChatMsgVO>
     */
    List<RoomChatGlmChatMsgVO> entityToVO(List<RoomChatGlmMsgDO> roomChatGlmMsgDOList);
}
