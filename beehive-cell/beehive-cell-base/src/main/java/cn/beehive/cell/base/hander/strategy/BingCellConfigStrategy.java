package cn.beehive.cell.base.hander.strategy;

import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.cell.base.enums.BingCellConfigCodeEnum;
import cn.beehive.cell.base.enums.ICellConfigCodeEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hncboy
 * @date 2023/5/29
 * NewBing cell 配置参数校验
 */
@Component
public class BingCellConfigStrategy extends AbstractCellConfigStrategy {

    @Override
    public CellCodeEnum getCellCode() {
        return CellCodeEnum.NEW_BING;
    }

    @Override
    public Class<? extends ICellConfigCodeEnum<?>> getCellConfigCodeEnumClazz() {
        return BingCellConfigCodeEnum.class;
    }

    @Override
    public void validate(ICellConfigCodeEnum<?> cellConfigCode, String roomConfigParamValue) {

    }

    /**
     * 获取房间配置参数
     *
     * @param roomId 房间 id
     * @return 房间配置参数
     */
    public Map<BingCellConfigCodeEnum, DataWrapper> getRoomConfigParamAsMap(Long roomId) {
        return getRoomConfigParamAsMap(roomId, getCellCode(), BingCellConfigCodeEnum.class);
    }
}
