package com.hncboy.beehive.cell.core.hander.strategy;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.cell.core.domain.bo.RoomConfigParamBO;

import java.util.List;
import java.util.Map;

/**
 * @author hncboy
 * @date 2023/5/29
 * 填写注释
 */
public interface CellConfigStrategy {

    /**
     * 获取 cell code 枚举
     *
     * @return cell code 枚举
     */
    CellCodeEnum getCellCode();

    /**
     * 获取 cell 配置项 code 枚举 Map
     *
     * @return cell 配置项 code 枚举 Map
     */
    <T extends ICellConfigCodeEnum> Map<String, T> getCellConfigCodeMap();

    /**
     * 获取 cell 配置项列表
     *
     * @return 配置项列表
     */
    <T extends ICellConfigCodeEnum> Map<T, DataWrapper> getCellConfigMap();

    /**
     * 获取房间配置参数
     *
     * @param roomId 房间 id
     * @return 房间配置参数
     */
    <T extends ICellConfigCodeEnum> Map<T, DataWrapper> getRoomConfigParamAsMap(Long roomId);

    /**
     * 单个校验房间配置项参数
     * TODO 是否需要采用规则引擎
     *
     * @param cellConfigCodeEnum 配置项 code 枚举
     * @param dataWrapper        房间配置项参数值
     */
    void singleValidate(ICellConfigCodeEnum cellConfigCodeEnum, DataWrapper dataWrapper);

    /**
     * 复合校验房间配置项参数
     *
     * @param roomConfigParamBOList 房间配置项参数业务对象列表
     */
    void compositeValidate(List<RoomConfigParamBO> roomConfigParamBOList);
}
