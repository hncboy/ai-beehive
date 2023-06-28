package com.hncboy.beehive.cell.core.hander.converter;

import com.hncboy.beehive.base.domain.entity.RoomDO;
import com.hncboy.beehive.cell.core.domain.vo.RoomListVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间相关转换
 */
@Mapper
public interface RoomConverter {

    RoomConverter INSTANCE = Mappers.getMapper(RoomConverter.class);

    /**
     * List<RoomDO> 转 List<RoomListVO>
     *
     * @param roomDOList List<RoomDO>
     * @return List<RoomListVO>
     */
    List<RoomListVO> entityToListVO(List<RoomDO> roomDOList);

    /**
     * RoomDO 转 RoomListVO
     *
     * @param roomDO RoomDO
     * @return RoomListVO
     */
    RoomListVO entityToListVO(RoomDO roomDO);

    /**
     * entityToListVO 后置处理
     *
     * @param roomDO     RoomDO
     * @param roomListVO RoomListVO
     */
    @AfterMapping
    default void afterEntityToListVO(RoomDO roomDO, @MappingTarget RoomListVO roomListVO) {
        roomListVO.setRoomId(roomDO.getId());
        roomListVO.setIsPinned(roomDO.getPinTime() > 0);
    }
}
