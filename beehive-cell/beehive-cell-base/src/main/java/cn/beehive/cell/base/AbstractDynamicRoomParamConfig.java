package cn.beehive.cell.base;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.cell.base.domain.bo.DynamicRoomParam;
import cn.beehive.cell.base.domain.bo.RoomParamKey;
import cn.beehive.cell.base.mapper.RoomConfigParamMapper;
import jakarta.annotation.Resource;

import java.util.Map;

/**
 * 动态的Room Param配置类
 *
 * @author CoDeleven
 */
public abstract class AbstractDynamicRoomParamConfig {
    @Resource
    private CellConfigPropertyManager configProperty;
    @Resource
    private RoomConfigParamMapper roomParamMapper;

    private Map<RoomParamKey, DynamicRoomParam> roomConfigParamCache;

    public AbstractDynamicRoomParamConfig() {
        // 从 configProperty 中载入属性
        Map<String, CellConfigDO> allCellConfigProperty = configProperty.getAll();
        // 初始化RoomParam
        initRoomParam(allCellConfigProperty);
    }

    /**
     * 根据当前 Cell 的所有配置项属性，初始化Room的所有参数
     * 如果是 System 级别需要判断一下
     * 如果是 User 级别，可以不用实现该方法
     *
     * @param allCellConfigProperty 当前Cell的所有配置项属性
     */
    protected abstract void initRoomParam(Map<String, CellConfigDO> allCellConfigProperty);
}
