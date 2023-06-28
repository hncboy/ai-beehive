package com.hncboy.beehive.cell.midjourney.handler.cell;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.cell.core.hander.strategy.AbstractCellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/5/30
 * Midjourney cell 配置项策略
 */
@Component
public class MidjourneyCellConfigStrategy extends AbstractCellConfigStrategy {

    @Override
    public CellCodeEnum getCellCode() {
        return CellCodeEnum.MIDJOURNEY;
    }

    @Override
    public Class<? extends ICellConfigCodeEnum> getCellConfigCodeEnumClazz() {
        return MidjourneyCellConfigCodeEnum.class;
    }
}
