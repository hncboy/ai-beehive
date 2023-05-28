package cn.beehive.cell.base;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.cell.base.domain.bo.RoomParamKey;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 系统级 Room的参数配置
 *
 * @author CoDeleven
 */
@Slf4j
public class SystemDynamicRoomParamConfig extends AbstractDynamicRoomParamConfig {

    private static final Integer SYSTEM_USER_ID = 0;
    private static final Integer SYSTEM_ROOM_ID = 0;


    @Override
    protected List<RoomConfigParamDO> initRoomParam(Map<String, CellConfigDO> allCellConfigProperty) {
        // 判断 系统级别的默认数据是否已经持久化
        for (Map.Entry<String, CellConfigDO> entry : allCellConfigProperty.entrySet()) {
            CellConfigDO value = entry.getValue();
            // 持久化系统默认数据
            persistentConfigItem(value);
            // 将数据存入内存中
        }
        return null;
    }

    private void persistentConfigItem(CellConfigDO cellConfigItem) {
        RoomConfigParamDO dbParam = roomParamMapper.selectOne(new LambdaQueryWrapper<RoomConfigParamDO>()
                .eq(RoomConfigParamDO::getConfigId, cellConfigItem.getId())
                .eq(RoomConfigParamDO::getIsDeleted, 0));
        if (Objects.isNull(dbParam)) {
            // TODO 将cellConfigItem插入
        }
        this.cacheConfigItem(RoomParamKey.builder().roomId(SYSTEM_ROOM_ID)
                .configId(cellConfigItem.getId())
                .userId(SYSTEM_USER_ID).build(), cellConfigItem, dbParam);
    }
}
