package cn.beehive.cell.base.hander.strategy;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.cell.base.enums.ICellConfigCodeEnum;
import cn.beehive.cell.base.service.CellConfigService;
import cn.beehive.cell.base.service.RoomConfigParamService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/29
 * 填写注释
 */
public abstract class AbstractCellConfigStrategy implements CellConfigStrategy {

    /**
     * 获取房间配置项参数
     *
     * @param roomId   房间 ID
     * @param cellCode cell 编码
     * @param clazz    枚举类
     * @param <T>      枚举类型
     * @return 房间配置项参数
     */
    public <T extends ICellConfigCodeEnum<T>> Map<T, DataWrapper> getRoomConfigParamAsMap(Long roomId, CellCodeEnum cellCode, Class<T> clazz) {
        // 获取房间配置项参数列表
        List<RoomConfigParamDO> roomConfigParams = SpringUtil.getBean(RoomConfigParamService.class).list(new LambdaQueryWrapper<RoomConfigParamDO>()
                .eq(RoomConfigParamDO::getRoomId, roomId));
        // 获取 cell 配置项列表
        List<CellConfigDO> cellConfigDOList = SpringUtil.getBean(CellConfigService.class)
                .list(new LambdaQueryWrapper<CellConfigDO>().eq(CellConfigDO::getCellCode, cellCode.getCode()));

        // 将房间配置项列表转为 Map
        Map<String, String> roomConfigParamMap = roomConfigParams.stream()
                .collect(Collectors.toMap(RoomConfigParamDO::getCellConfigCode, RoomConfigParamDO::getValue));

        // 将 cell 配置项列表转为 Map
        Map<String, String> cellConfigMap = cellConfigDOList.stream()
                .collect(Collectors.toMap(CellConfigDO::getCode, CellConfigDO::getDefaultValue));

        // 将房间配置项参数覆盖 cell 配置项
        cellConfigMap.putAll(roomConfigParamMap);

        // 创建新的 Map，将 key 转换为相应的枚举类型
        Map<T, DataWrapper> resultMap = new HashMap<>(cellConfigMap.size());

        // 获取枚举常量数组
        T[] enumConstants = clazz.getEnumConstants();
        if (enumConstants.length == 0) {
            return resultMap;
        }
        Map<String, T> codeMap = enumConstants[0].getCodeMap();
        for (Map.Entry<String, String> entry : cellConfigMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            T enumValue = codeMap.get(key);
            if (enumValue != null) {
                resultMap.put(enumValue, new DataWrapper(value));
            }
        }
        return resultMap;
    }
}
