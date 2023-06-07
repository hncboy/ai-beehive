package cn.beehive.cell.core.hander.strategy;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.cell.core.cache.CellConfigCache;
import cn.beehive.cell.core.domain.bo.RoomConfigParamBO;
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
        // 获取 cell 配置项列表
        List<CellConfigDO> cellConfigDOList = CellConfigCache.listCellConfig(getCellCode());
        // TODO 暂不考虑一些条件变更用户需要重新输入的情况，比如原先非必填，现在必填
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
    public void singleValidate(ICellConfigCodeEnum cellConfigCodeEnum, DataWrapper dataWrapper) {
        cellConfigCodeEnum.singleValidate(dataWrapper);
    }

    @Override
    public void compositeValidate(List<RoomConfigParamBO> roomConfigParamBOList) {
        Map<String, ICellConfigCodeEnum> cellConfigCodeMap = getCellConfigCodeMap();

        // 房间配置项
        Map<ICellConfigCodeEnum, RoomConfigParamBO> roomConfigParamBOMap = roomConfigParamBOList.stream()
                .collect(Collectors.toMap(bo -> cellConfigCodeMap.get(bo.getCellConfigCode()), Function.identity()));

        // 遍历所有配置项，先针对单个参数进行校验
        for (Map.Entry<ICellConfigCodeEnum, RoomConfigParamBO> entry : roomConfigParamBOMap.entrySet()) {
            singleValidate(entry.getKey(), new DataWrapper(entry.getValue().getDefaultValue()));
        }

        // 针对用户填的配置项进行复合校验，这里就先不对所有配置项进行复合校验，需要的话要改动校验规则
        for (Map.Entry<ICellConfigCodeEnum, RoomConfigParamBO> entry : roomConfigParamBOMap.entrySet()) {
            RoomConfigParamBO roomConfigParamBO = entry.getValue();
            ICellConfigCodeEnum cellConfigCodeEnum = entry.getKey();

            // 复合校验所有的参数
            cellConfigCodeEnum.compositeValidate(roomConfigParamBOMap, getCellCode());

            // 找到不使用默认值的，复合校验自己的参数
            if (roomConfigParamBO.getCellConfigCode().equals(cellConfigCodeEnum.getCode()) && !roomConfigParamBO.getIsUseDefaultValue()) {
                cellConfigCodeEnum.compositeValidateSelf(roomConfigParamBOMap, getCellCode());
            }
        }
    }
}
