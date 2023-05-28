package cn.beehive.cell.base.hander.strategy;

import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.cell.base.enums.ICellConfigCodeEnum;

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
     * 校验
     *
     * @param cellConfigCodeEnum   cell 配置项 code 枚举
     * @param roomConfigParamValue 房间配置项参数值
     */
    void validate(ICellConfigCodeEnum cellConfigCodeEnum, String roomConfigParamValue);
}
