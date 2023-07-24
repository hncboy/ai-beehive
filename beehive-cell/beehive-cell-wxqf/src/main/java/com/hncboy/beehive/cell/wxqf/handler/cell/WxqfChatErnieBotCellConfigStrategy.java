package com.hncboy.beehive.cell.wxqf.handler.cell;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.cell.core.hander.strategy.AbstractCellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import com.hncboy.beehive.cell.wxqf.enums.WxqfChatErnieBotCellConfigCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/7/24
 * 文心千帆对话 ERNIE-Bot 配置项策略
 */
@Component
public class WxqfChatErnieBotCellConfigStrategy extends AbstractCellConfigStrategy {

    @Override
    public CellCodeEnum getCellCode() {
        return CellCodeEnum.WXQF_ERNIE_BOT;
    }

    @Override
    public Class<? extends ICellConfigCodeEnum> getCellConfigCodeEnumClazz() {
        return WxqfChatErnieBotCellConfigCodeEnum.class;
    }
}