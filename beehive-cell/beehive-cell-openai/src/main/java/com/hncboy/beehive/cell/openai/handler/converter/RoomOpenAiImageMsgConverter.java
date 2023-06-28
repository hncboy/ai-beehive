package com.hncboy.beehive.cell.openai.handler.converter;

import com.hncboy.beehive.base.domain.entity.RoomOpenAiImageMsgDO;
import com.hncboy.beehive.cell.openai.domain.vo.RoomOpenAiImageMsgVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/3
 * OpenAi 图像房间消息相关转换
 */
@Mapper
public interface RoomOpenAiImageMsgConverter {

    RoomOpenAiImageMsgConverter INSTANCE = Mappers.getMapper(RoomOpenAiImageMsgConverter.class);

    /**
     * List<RoomOpenAiImageMsgDO> 转 List<RoomOpenAiImageMsgVO>
     *
     * @param roomOpenAiImageMsgDOList List<RoomOpenAiImageMsgDO>
     * @return List<RoomOpenAiImageMsgVO>
     */
    List<RoomOpenAiImageMsgVO> entityToVO(List<RoomOpenAiImageMsgDO> roomOpenAiImageMsgDOList);

    /**
     * RoomOpenAiImageMsgDO 转 RoomOpenAiImageMsgVO
     *
     * @param roomOpenAiImageMsgDO RoomOpenAiImageMsgDO
     * @return RoomOpenAiImageMsgVO
     */
    RoomOpenAiImageMsgVO entityToVO(RoomOpenAiImageMsgDO roomOpenAiImageMsgDO);

    @AfterMapping
    default void afterEntityToVO(RoomOpenAiImageMsgDO roomOpenAiImageMsgDO, @MappingTarget RoomOpenAiImageMsgVO roomOpenAiImageMsgVO) {
        roomOpenAiImageMsgVO.setImageUrl(roomOpenAiImageMsgDO.getImageName());
    }
}
