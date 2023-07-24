package com.hncboy.beehive.cell.wxqf.handler.converter;

import com.hncboy.beehive.base.domain.entity.RoomWxqfChatMsgDO;
import com.hncboy.beehive.cell.wxqf.domain.vo.RoomWxqfChatMsgVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话房间消息相关转换
 */
@Mapper
public interface RoomWxqfChatMsgConverter {

    RoomWxqfChatMsgConverter INSTANCE = Mappers.getMapper(RoomWxqfChatMsgConverter.class);

    /**
     * List<RoomWxqfChatMsgDO> 转 List<RoomWxqfChatMsgVO>
     *
     * @param roomWxqfChatMsgDOList List<RoomWxqfChatMsgDO>
     * @return List<RoomWxqfChatMsgVO>
     */
    List<RoomWxqfChatMsgVO> entityToVO(List<RoomWxqfChatMsgDO> roomWxqfChatMsgDOList);
}
