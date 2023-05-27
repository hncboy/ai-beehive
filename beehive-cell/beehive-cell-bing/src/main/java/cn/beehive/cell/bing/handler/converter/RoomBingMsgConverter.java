package cn.beehive.cell.bing.handler.converter;

import cn.beehive.base.domain.entity.RoomBingMsgDO;
import cn.beehive.cell.bing.domain.vo.RoomBingMsgVO;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/26
 * Bing 房间消息相关转换
 */
public interface RoomBingMsgConverter {

    RoomBingMsgConverter INSTANCE = Mappers.getMapper(RoomBingMsgConverter.class);

    /**
     * List<RoomBingMsgDO> 转 List<RoomBingMsgVO>
     *
     * @param roomBingMsgDOList List<RoomBingMsgDO>
     * @return List<RoomBingMsgVO>
     */
    List<RoomBingMsgVO> entityToVO(List<RoomBingMsgDO> roomBingMsgDOList);
}
