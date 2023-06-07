package cn.beehive.cell.core.cache;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.cell.core.service.CellConfigService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/7
 * Cell 配置项缓存
 */
public class CellConfigCache {

    /**
     * 获取 cell 配置项列表
     *
     * @param cellCodeEnum cell 配置项编码
     * @return cell 配置项列表
     */
    public static List<CellConfigDO> listCellConfig(CellCodeEnum cellCodeEnum) {
        // TODO Redis 缓存
        return SpringUtil.getBean(CellConfigService.class)
                .list(new LambdaQueryWrapper<CellConfigDO>().eq(CellConfigDO::getCellCode, cellCodeEnum));
    }
}
