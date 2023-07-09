package com.hncboy.beehive.cell.core.hander.strategy;

import com.hncboy.beehive.base.domain.entity.CellConfigDO;
import com.hncboy.beehive.base.domain.entity.RoomConfigParamDO;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.cell.core.cache.CellConfigCache;
import com.hncboy.beehive.cell.core.cache.RoomConfigParamCache;
import com.hncboy.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import com.hncboy.beehive.cell.core.domain.bo.RoomConfigParamBO;
import com.hncboy.beehive.cell.core.hander.CellConfigPermissionHandler;
import cn.hutool.core.util.StrUtil;

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

    /**
     * 获取 cell 配置项 code 枚举 Class
     *
     * @return cell 配置项 code 枚举 Class
     */
    public abstract Class<? extends ICellConfigCodeEnum> getCellConfigCodeEnumClazz();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ICellConfigCodeEnum> Map<String, T> getCellConfigCodeMap() {
        return (Map<String, T>) Arrays.stream(getCellConfigCodeEnumClazz().getEnumConstants())
                .collect(Collectors.toMap(ICellConfigCodeEnum::getCode, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ICellConfigCodeEnum> Map<T, DataWrapper> getCellConfigMap() {
        Map<String, ICellConfigCodeEnum> cellConfigCodeMap = getCellConfigCodeMap();
        List<CellConfigDO> cellConfigDOList = CellConfigCache.listCellConfig(getCellCode());
        // 将 cell 配置项列表转为 Map
        return (Map<T, DataWrapper>) cellConfigDOList.stream()
                .collect(Collectors.toMap(entity -> cellConfigCodeMap.get(entity.getCode()), entity -> new DataWrapper(entity.getDefaultValue())));
    }

    @Override
    public <T extends ICellConfigCodeEnum> Map<T, DataWrapper> getRoomConfigParamAsMap(Long roomId) {
        // 获取房间配置项参数列表
        List<RoomConfigParamDO> roomConfigParams = RoomConfigParamCache.getRoomConfigParam(roomId);

        // 将房间配置项列表转为 Map
        Map<String, DataWrapper> roomConfigParamMap = roomConfigParams.stream()
                .collect(Collectors.toMap(RoomConfigParamDO::getCellConfigCode, entity -> new DataWrapper(entity.getValue())));

        List<CellConfigPermissionBO> cellConfigPermissionBOList = CellConfigPermissionHandler.listCellConfigPermission(getCellCode());
        for (CellConfigPermissionBO cellConfigPermissionBO : cellConfigPermissionBOList) {
            // 用户自己填的就不判断 TODO 暂不考虑原先用户可以修改但是现在不能修改的情况
            if (roomConfigParamMap.containsKey(cellConfigPermissionBO.getCellConfigCode())) {
                continue;
            }

            // 存在情况：原本可以用系统的默认值或者非必填，现在需要用户填写
            // 如果是必填项，但是用户没有填写，也没有默认值，抛出异常
            if (cellConfigPermissionBO.getIsRequired() && !CellConfigPermissionHandler.isCanUseDefaultValue(cellConfigPermissionBO)) {
                throw new ServiceException(StrUtil.format("房间配置项参数[{}]需要补充完整", cellConfigPermissionBO.getName()));
            }
        }

        // 获取 Cell 配置项权限 Map
        Map<String, DataWrapper> cellConfigPermissionBoMap = cellConfigPermissionBOList.stream()
                .collect(Collectors.toMap(CellConfigPermissionBO::getCellConfigCode, bo -> new DataWrapper(bo.getDefaultValue())));

        // 将房间配置项参数覆盖 cell 配置项
        cellConfigPermissionBoMap.putAll(roomConfigParamMap);

        // 获取枚举常量数组，将其转为 code map
        Map<String, T> cellConfigCodeMap = getCellConfigCodeMap();

        // 遍历 cellConfigMap，将 key 转换为相应的枚举类型
        Map<T, DataWrapper> resultMap = new HashMap<>(cellConfigPermissionBoMap.size());
        for (Map.Entry<String, DataWrapper> entry : cellConfigPermissionBoMap.entrySet()) {
            if (cellConfigCodeMap.containsKey(entry.getKey())) {
                resultMap.put(cellConfigCodeMap.get(entry.getKey()), entry.getValue());
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
        Map<ICellConfigCodeEnum, RoomConfigParamBO> roomConfigParamBoMap = roomConfigParamBOList.stream()
                .collect(Collectors.toMap(bo -> cellConfigCodeMap.get(bo.getCellConfigCode()), Function.identity()));

        // 遍历所有配置项，先针对单个参数进行校验
        for (Map.Entry<ICellConfigCodeEnum, RoomConfigParamBO> entry : roomConfigParamBoMap.entrySet()) {
            singleValidate(entry.getKey(), new DataWrapper(entry.getValue().getDefaultValue()));
        }

        // 针对用户填的配置项进行复合校验，这里就先不对所有配置项进行复合校验，需要的话要改动校验规则
        for (Map.Entry<ICellConfigCodeEnum, RoomConfigParamBO> entry : roomConfigParamBoMap.entrySet()) {
            RoomConfigParamBO roomConfigParamBO = entry.getValue();
            ICellConfigCodeEnum cellConfigCodeEnum = entry.getKey();

            // 单个校验参数
            singleValidate(cellConfigCodeEnum, new DataWrapper(roomConfigParamBO.getValue()));

            // 复合校验所有的参数
            cellConfigCodeEnum.compositeValidate(roomConfigParamBoMap, getCellCode());

            // 找到不使用默认值的，复合校验自己的参数
            if (roomConfigParamBO.getCellConfigCode().equals(cellConfigCodeEnum.getCode()) && !roomConfigParamBO.getIsUseDefaultValue()) {
                cellConfigCodeEnum.compositeValidateSelf(roomConfigParamBoMap, getCellCode());
            }
        }
    }
}
