package com.hncboy.beehive.cell.wxqf.handler.cell;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.cell.core.hander.strategy.AbstractCellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import com.hncboy.beehive.cell.wxqf.enums.WxqfChatBloomz7bCellConfigCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话 BLOOMZ-7B 配置项策略
 */
@Component
public class WxqfChatBloomz7bCellConfigStrategy extends AbstractCellConfigStrategy {

    @Override
    public CellCodeEnum getCellCode() {
        return CellCodeEnum.WXQF_BLOOMZ_7B;
    }

    @Override
    public Class<? extends ICellConfigCodeEnum> getCellConfigCodeEnumClazz() {
        return WxqfChatBloomz7bCellConfigCodeEnum.class;
    }
}