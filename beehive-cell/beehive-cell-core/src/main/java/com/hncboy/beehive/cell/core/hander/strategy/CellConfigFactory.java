package com.hncboy.beehive.cell.core.hander.strategy;

import com.hncboy.beehive.base.domain.entity.RoomDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.cell.core.hander.RoomHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
     * @param roomId              房间 id
     * @param limitedCellCodeEnum 限制的 cell code
     * @return cell 配置项策略
     */
    public CellConfigStrategy getCellConfigStrategy(Long roomId, CellCodeEnum limitedCellCodeEnum) {
        return getCellConfigStrategy(roomId, Collections.singletonList(limitedCellCodeEnum));
    }


    /**
     * 获取 cell 配置项策略
     *
     * @param roomId               房间 id
     * @param limitedCellCodeEnums 限制的 cell code 列表
     * @return cell 配置项策略
     */
    public CellConfigStrategy getCellConfigStrategy(Long roomId, List<CellCodeEnum> limitedCellCodeEnums) {
        RoomDO roomDO = RoomHandler.checkRoomExistAndCellCanUse(roomId, limitedCellCodeEnums);
        return getCellConfigStrategy(roomDO.getCellCode());
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
                .orElseThrow(() -> new ServiceException("无效的 cell code: " + cellCode));
    }
}
