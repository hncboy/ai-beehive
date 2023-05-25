package cn.beehive.cell.base.domain.bo;

import cn.beehive.base.domain.entity.RoomConfigParamDO;
import lombok.Data;

/**
 * 动态的RoomParam
 *
 * @author CoDeleven
 */
@Data
public class DynamicRoomParam {
    private String value;
    private RoomConfigParamDO paramEntity;
}
