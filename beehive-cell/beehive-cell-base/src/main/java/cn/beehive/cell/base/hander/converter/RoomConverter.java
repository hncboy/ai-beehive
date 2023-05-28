package cn.beehive.cell.base.hander.converter;

import cn.beehive.base.domain.entity.RoomDO;
import cn.beehive.cell.base.domain.vo.RoomListVO;
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

    @AfterMapping
    default void entityToListVO(RoomDO roomDO, @MappingTarget RoomListVO roomListVO) {
        roomListVO.setIsPinned(roomDO.getPinTime() > 0);
    }
}
