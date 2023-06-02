package cn.beehive.cell.openai.handler.cell;

import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.cell.core.hander.strategy.AbstractCellConfigStrategy;
import cn.beehive.cell.core.hander.strategy.ICellConfigCodeEnum;
import cn.beehive.cell.openai.enums.OpenAiImageCellConfigCodeEnum;
import org.springframework.stereotype.Component;

/**
 * @author hncboy
 * @date 2023/6/3
 * OpenAi 图像配置项策略
 */
@Component
public class OpenAiImageCellConfigStrategy extends AbstractCellConfigStrategy {

    @Override
    public CellCodeEnum getCellCode() {
        return CellCodeEnum.OPENAI_IMAGE;
    }

    @Override
    public Class<? extends ICellConfigCodeEnum> getCellConfigCodeEnumClazz() {
        return OpenAiImageCellConfigCodeEnum.class;
    }
}
