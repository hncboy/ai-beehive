package cn.beehive.cell.core.hander.strategy;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.cell.core.service.CellConfigService;
import cn.beehive.cell.core.service.RoomConfigParamService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置项策略抽象类
 */
public abstract class AbstractCellConfigStrategy implements CellConfigStrategy {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ICellConfigCodeEnum> Map<String, T> getCellConfigCodeMap() {
        return (Map<String, T>) Arrays.stream(getCellConfigCodeEnumClazz().getEnumConstants())
                .collect(Collectors.toMap(ICellConfigCodeEnum::getCode, Function.identity()));
    }

    @Override
    public <T extends ICellConfigCodeEnum> Map<T, DataWrapper> getRoomConfigParamAsMap(Long roomId) {
        // 获取房间配置项参数列表
        List<RoomConfigParamDO> roomConfigParams = SpringUtil.getBean(RoomConfigParamService.class).list(new LambdaQueryWrapper<RoomConfigParamDO>()
                .eq(RoomConfigParamDO::getRoomId, roomId));
        // 获取 cell 配置项列表 TODO Redis 缓存
        List<CellConfigDO> cellConfigDOList = SpringUtil.getBean(CellConfigService.class)
                .list(new LambdaQueryWrapper<CellConfigDO>().eq(CellConfigDO::getCellCode, getCellCode()));
        // TODO 暂不考虑一些条件变更用户需要重新输入的情况
        // 将房间配置项列表转为 Map
        Map<String, String> roomConfigParamMap = roomConfigParams.stream()
                .collect(Collectors.toMap(RoomConfigParamDO::getCellConfigCode, RoomConfigParamDO::getValue));

        // 将 cell 配置项列表转为 Map
        Map<String, String> cellConfigMap = cellConfigDOList.stream()
                .collect(Collectors.toMap(CellConfigDO::getCode, CellConfigDO::getDefaultValue));

        // 将房间配置项参数覆盖 cell 配置项
        cellConfigMap.putAll(roomConfigParamMap);

        // 获取枚举常量数组，将其转为 code map
        Map<String, T> cellConfigCodeMap = getCellConfigCodeMap();

        // 遍历 cellConfigMap，将 key 转换为相应的枚举类型
        Map<T, DataWrapper> resultMap = new HashMap<>(cellConfigMap.size());
        for (Map.Entry<String, String> entry : cellConfigMap.entrySet()) {
            if (cellConfigCodeMap.containsKey(entry.getKey())) {
                T cellConfigCode = cellConfigCodeMap.get(entry.getKey());
                resultMap.put(cellConfigCode, new DataWrapper(entry.getValue()));
            }
        }
        return resultMap;
    }

    @Override
    public void validate(ICellConfigCodeEnum cellConfigCodeEnum, DataWrapper roomConfigParamValue) {
        // 先通用的校验一遍
        cellConfigCodeEnum.firstValidate(roomConfigParamValue);
        // 有需要的子类可以各自实现这个再校验一遍
        nextValidate(cellConfigCodeEnum, roomConfigParamValue);
    }

    @Override
    public void validate(Map<? extends ICellConfigCodeEnum, DataWrapper> roomConfigParamMap) {
        // 待开发
    }

    /**
     * 下一步校验房间配置项参数
     *
     * @param cellConfigCodeEnum   房间配置项 code 枚举
     * @param roomConfigParamValue 房间配置项参数值
     */
    public void nextValidate(ICellConfigCodeEnum cellConfigCodeEnum, DataWrapper roomConfigParamValue) {
        // 有需要的自己继承
    }
}
