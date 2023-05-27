package cn.beehive.cell.midjourney.handler.converter;

import cn.beehive.base.domain.entity.RoomMjMsgDO;
import cn.beehive.cell.midjourney.domain.vo.RoomMjMsgVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/24
 * Midjourney 房间消息相关转换
 */
@Mapper
public interface RoomMjMsgConverter {

    RoomMjMsgConverter INSTANCE = Mappers.getMapper(RoomMjMsgConverter.class);

    /**
     * List<RoomMjMsgDO> 转 List<RoomMjMsgVO>
     *
     * @param roomMjMsgDOList List<RoomMjMsgDO>
     * @return List<RoomMjMsgVO>
     */
    List<RoomMjMsgVO> entityToVO(List<RoomMjMsgDO> roomMjMsgDOList);

    @AfterMapping
    default void afterEntityToVO(RoomMjMsgDO roomMjMsgDO, @MappingTarget RoomMjMsgVO roomMjMsgVO) {
        roomMjMsgVO.setImageUrl(roomMjMsgDO.getImageName());
        // TODO
        switch (roomMjMsgDO.getAction()) {
            case IMAGINE -> {

            }
            case UPSCALE -> {

            }
            case VARIATION -> {

            }
            case DESCRIBE -> {

            }
        }
    }
}
