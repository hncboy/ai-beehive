package cn.beehive.cell.core.hander.strategy;

import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置项工厂
 */
@Component
public class CellConfigFactory {

    private final Map<CellCodeEnum, CellConfigStrategy> strategies = new HashMap<>();

    public CellConfigFactory(List<CellConfigStrategy> strategyList) {
        for (CellConfigStrategy strategy : strategyList) {
            strategies.put(strategy.getCellCode(), strategy);
        }
    }

    /**
     * 获取 cell 配置项策略
     *
     * @param cellCode cell code
     * @return cell 配置项策略
     */
    public CellConfigStrategy getCellConfigStrategy(CellCodeEnum cellCode) {
        return Optional
                .ofNullable(strategies.get(cellCode))
                .orElseThrow(() -> new ServiceException("Invalid cell code: " + cellCode));
    }
}
