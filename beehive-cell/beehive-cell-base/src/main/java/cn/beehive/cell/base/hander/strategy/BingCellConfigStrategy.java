package cn.beehive.cell.base.hander.strategy;

import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.cell.base.enums.BingCellConfigCodeEnum;
import cn.beehive.cell.base.enums.ICellConfigCodeEnum;

/**
 * @author hncboy
 * @date 2023/5/29
 * NewBing cell 配置参数校验
 */
public class BingCellConfigStrategy implements CellConfigStrategy {

    @Override
    public CellCodeEnum getCellCode() {
        return CellCodeEnum.NEW_BING;
    }

    @Override
    public Class<? extends ICellConfigCodeEnum> getCellConfigCodeEnumClazz() {
        return BingCellConfigCodeEnum.class;
    }

    @Override
    public void validate(ICellConfigCodeEnum cellConfigCode, String roomConfigParamValue) {

    }
}
