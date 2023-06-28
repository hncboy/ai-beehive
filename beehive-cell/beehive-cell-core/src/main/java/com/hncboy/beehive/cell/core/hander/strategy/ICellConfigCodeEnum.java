package com.hncboy.beehive.cell.core.hander.strategy;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.cell.core.domain.bo.RoomConfigParamBO;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置参数枚举接口
 */
public interface ICellConfigCodeEnum {

    /**
     * 获取配置参数 code
     *
     * @return 配置参数 code
     */
    String getCode();

    /**
     * 校验单个配置项参数值
     *
     * @param dataWrapper 配置参数值
     */
    default void singleValidate(DataWrapper dataWrapper) {

    }

    /**
     * 复合校验
     * 需要结合其他配置项参数值进行校验
     *
     * @param roomConfigParamMap 房间配置项参数业务对象 Map
     * @param cellCode           cellCode
     */
    default void compositeValidate(Map<ICellConfigCodeEnum, RoomConfigParamBO> roomConfigParamMap, CellCodeEnum cellCode) {

    }

    /**
     * 复合校验自己的参数
     *
     * @param roomConfigParamMap 房间配置项参数业务对象 Map
     * @param cellCode           cellCode
     */
    default void compositeValidateSelf(Map<ICellConfigCodeEnum, RoomConfigParamBO> roomConfigParamMap, CellCodeEnum cellCode) {

    }
}
