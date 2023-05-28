package cn.beehive.cell.base;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.cell.base.domain.bo.DynamicRoomParam;
import cn.beehive.cell.base.domain.bo.RoomParamKey;
import cn.beehive.cell.base.mapper.RoomConfigParamMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 动态的Room Param配置类
 *
 * @author CoDeleven
 */
public abstract class AbstractDynamicRoomParamConfig {
    @Resource
    protected CellConfigPropertyManager configProperty;
    @Resource
    protected RoomConfigParamMapper roomParamMapper;

    private Map<RoomParamKey, DynamicRoomParam> roomConfigParamCache;

    public AbstractDynamicRoomParamConfig() {
        // 从 configProperty 中载入属性
        Map<String, CellConfigDO> allCellConfigProperty = configProperty.getAll();
        // 初始化RoomParam
        initRoomParam(allCellConfigProperty);
    }

    // TODO 更新配置项操作，直接更新roomConfigParamCache和库中数据

    /**
     * 提供的获取 Room 的实际 Param 的操作方法
     *
     * @param roomId   实际模型ID
     * @param userId   用户ID
     * @param configId 配置项ID
     * @return 该配置项的Int值
     */
    public Integer getRoomParamAsInt(Integer roomId, Integer userId, Integer configId) {
        RoomParamKey key = RoomParamKey.builder().roomId(roomId).userId(userId).configId(configId).build();
        DynamicRoomParam param = roomConfigParamCache.get(key);
        if (Objects.isNull(param)) {
            RoomConfigParamDO inDb = roomParamMapper.selectOne(new LambdaQueryWrapper<RoomConfigParamDO>().eq(RoomConfigParamDO::getConfigId, configId)
                    .eq(RoomConfigParamDO::getUserId, userId).eq(RoomConfigParamDO::getRoomId, roomId));
            if (Objects.nonNull(inDb)) {
                roomConfigParamCache.put(key, new DynamicRoomParam(inDb));
            }
            // TODO 直接返回空？
            return null;
        }
        return Integer.parseInt(param.getParamEntity().getValue());
    }

    /**
     * 将每个系统级别的配置项缓存起来
     *
     * @param key            全局范围内标识配置项，系统级，用户级，作用于哪个Room、哪个userId
     * @param cellConfigItem cell配置项属性
     * @param dbParam        库中该Room的实际数据
     */
    protected void cacheConfigItem(RoomParamKey key, CellConfigDO cellConfigItem, RoomConfigParamDO dbParam) {
        roomConfigParamCache.put(key, new DynamicRoomParam(dbParam));
    }

    /**
     * 根据当前 Cell 的所有配置项属性，初始化Room的所有参数
     * 如果是 System 级别需要判断一下
     * 如果是 User 级别，可以不用实现该方法
     *
     * @param allCellConfigProperty 当前Cell的所有配置项属性
     */
    protected abstract List<RoomConfigParamDO> initRoomParam(Map<String, CellConfigDO> allCellConfigProperty);
}
