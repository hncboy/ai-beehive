package com.hncboy.beehive.cell.midjourney.handler.converter;

import com.hncboy.beehive.base.domain.entity.RoomMidjourneyMsgDO;
import com.hncboy.beehive.base.enums.MidjourneyMsgStatusEnum;
import com.hncboy.beehive.cell.midjourney.domain.vo.RoomMidjourneyMsgVO;
import com.hncboy.beehive.cell.midjourney.handler.MidjourneyTaskQueueHandler;
import cn.hutool.extra.spring.SpringUtil;
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
public interface RoomMidjourneyMsgConverter {

    RoomMidjourneyMsgConverter INSTANCE = Mappers.getMapper(RoomMidjourneyMsgConverter.class);

    /**
     * List<RoomMidjourneyMsgDO> 转 List<RoomMidjourneyMsgVO>
     *
     * @param roomMidjourneyMsgDOList List<RoomMidjourneyMsgDO>
     * @return List<RoomMidjourneyMsgVO>
     */
    List<RoomMidjourneyMsgVO> entityToVO(List<RoomMidjourneyMsgDO> roomMidjourneyMsgDOList);

    /**
     * RoomMidjourneyMsgDO 转 RoomMidjourneyMsgVO
     *
     * @param roomMidjourneyMsgDO RoomMidjourneyMsgDO
     * @return RoomMidjourneyMsgVO
     */
    RoomMidjourneyMsgVO entityToVO(RoomMidjourneyMsgDO roomMidjourneyMsgDO);

    /**
     * entityToVO 后置处理
     *
     * @param roomMidjourneyMsgDO roomMidjourneyMsgDO
     * @param roomMidjourneyMsgVO roomMidjourneyMsgVO
     */
    @AfterMapping
    default void afterEntityToVO(RoomMidjourneyMsgDO roomMidjourneyMsgDO, @MappingTarget RoomMidjourneyMsgVO roomMidjourneyMsgVO) {
        roomMidjourneyMsgVO.setCompressedImageUrl(roomMidjourneyMsgDO.getCompressedImageName());
        roomMidjourneyMsgVO.setOriginalImageUrl(roomMidjourneyMsgDO.getOriginalImageName());
        // 如果是排队中状态，查询当前排队的长度
        if (roomMidjourneyMsgDO.getStatus() == MidjourneyMsgStatusEnum.SYS_QUEUING) {
            roomMidjourneyMsgVO.setWaitQueueLength(SpringUtil.getBean(MidjourneyTaskQueueHandler.class).getWaitQueueLength());
        }
    }
}
