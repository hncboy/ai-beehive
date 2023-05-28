package cn.beehive.cell.base.domain.bo;

import cn.beehive.base.domain.entity.RoomConfigParamDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动态的RoomParam
 *
 * @author CoDeleven
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DynamicRoomParam {
    private RoomConfigParamDO paramEntity;
}
