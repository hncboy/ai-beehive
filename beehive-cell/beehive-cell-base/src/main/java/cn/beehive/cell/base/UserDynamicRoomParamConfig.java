package cn.beehive.cell.base;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 系统级 Room的参数配置
 *
 * @author CoDeleven
 */
@Slf4j
public class UserDynamicRoomParamConfig extends AbstractDynamicRoomParamConfig {

    @Override
    protected List<RoomConfigParamDO> initRoomParam(Map<String, CellConfigDO> allCellConfigProperty) {
        // 什么也不做
        return null;
    }

}
