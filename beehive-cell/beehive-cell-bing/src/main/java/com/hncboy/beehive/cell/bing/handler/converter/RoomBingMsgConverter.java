package com.hncboy.beehive.cell.bing.handler.converter;

import com.hncboy.beehive.base.domain.entity.RoomBingDO;
import com.hncboy.beehive.base.domain.entity.RoomBingMsgDO;
import com.hncboy.beehive.cell.bing.domain.bo.BingRoomBO;
import com.hncboy.beehive.cell.bing.domain.vo.RoomBingMsgVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * Bing 房间消息相关转换
 */
@Mapper
public interface RoomBingMsgConverter {

    RoomBingMsgConverter INSTANCE = Mappers.getMapper(RoomBingMsgConverter.class);

    /**
     * List<RoomBingMsgDO> 转 List<RoomBingMsgVO>
     *
     * @param roomBingMsgDOList List<RoomBingMsgDO>
     * @return List<RoomBingMsgVO>
     */
    List<RoomBingMsgVO> entityToVo(List<RoomBingMsgDO> roomBingMsgDOList);

    /**
     * RoomBingDO 转 RoomBingMsgVO
     *
     * @param roomBingDO RoomBingDO
     * @return RoomBingMsgVO
     */
    RoomBingMsgDO roomBingDoToEntity(RoomBingDO roomBingDO);

    /**
     * BingRoomBO 转 RoomBingMsgVO
     *
     * @param bingRoomBO BingRoomBO
     * @return RoomBingMsgVO
     */
    default RoomBingMsgDO bingRoomBoToEntity(BingRoomBO bingRoomBO) {
        RoomBingMsgDO roomBingMsgDO = roomBingDoToEntity(bingRoomBO.getRoomBingDO());
        roomBingMsgDO.setIsNewTopic(bingRoomBO.getIsNewTopic());
        roomBingMsgDO.setRefreshRoomReason(bingRoomBO.getRefreshRoomReason());
        return roomBingMsgDO;
    }
}
