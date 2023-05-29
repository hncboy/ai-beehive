package cn.beehive.cell.base.hander.strategy;

import cn.beehive.base.enums.CellCodeEnum;

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
     * 获取 cell 配置项 code 枚举 Class
     *
     * @return cell 配置项 code 枚举 Class
     */
    Class<? extends ICellConfigCodeEnum> getCellConfigCodeEnumClazz();

    /**
     * 获取 cell 配置项 code 枚举 Map
     *
     * @return cell 配置项 code 枚举 Map
     */
    <T extends ICellConfigCodeEnum> Map<String, T> getCellConfigCodeMap();

    /**
     * 获取房间配置参数
     *
     * @param roomId 房间 id
     * @return 房间配置参数
     */
    <T extends ICellConfigCodeEnum> Map<T, DataWrapper> getRoomConfigParamAsMap(Long roomId);

    /**
     * 校验房间配置项参数
     *
     * @param roomConfigParamMap 房间配置项参数值
     */
    void validate(Map<? extends ICellConfigCodeEnum, DataWrapper> roomConfigParamMap);

    /**
     * 校验房间配置项参数
     *
     * @param cellConfigCodeEnum   房间配置项 code 枚举
     * @param roomConfigParamValue 房间配置项参数值
     */
    void validate(ICellConfigCodeEnum cellConfigCodeEnum, DataWrapper roomConfigParamValue);
}
