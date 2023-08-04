package com.hncboy.beehive.cell.chatglm.handler.cell;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.cell.core.hander.strategy.AbstractCellConfigStrategy;
import com.hncboy.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import com.hncboy.beehive.cell.chatglm.enums.ChatGlmCellConfigCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author hanpeng
 * @date 2023/8/3
 *  对话 ChatGLM 配置项策略
 */
@Component
public class ChatGlmCellConfigStrategy extends AbstractCellConfigStrategy {

    @Override
    public CellCodeEnum getCellCode() {
        return CellCodeEnum.CHAT_GLM;
    }

    @Override
    public Class<? extends ICellConfigCodeEnum> getCellConfigCodeEnumClazz() {
        return ChatGlmCellConfigCodeEnum.class;
    }
}
