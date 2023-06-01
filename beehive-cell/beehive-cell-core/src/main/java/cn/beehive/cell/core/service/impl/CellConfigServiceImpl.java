package cn.beehive.cell.core.service.impl;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.mapper.CellConfigMapper;
import cn.beehive.cell.core.domain.vo.CellConfigVO;
import cn.beehive.cell.core.hander.CellHandler;
import cn.beehive.cell.core.hander.CellPermissionHandler;
import cn.beehive.cell.core.hander.converter.CellConfigConverter;
import cn.beehive.cell.core.service.CellConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置项业务接口实现类
 */
@Service
public class CellConfigServiceImpl extends ServiceImpl<CellConfigMapper, CellConfigDO> implements CellConfigService {

    @Override
    public List<CellConfigVO> listCellConfig(String cellCode) {
        CellCodeEnum cellCodeEnum = CellHandler.parseCellCodeStr(cellCode);
        CellHandler.checkCellPublishExist(cellCodeEnum);
        CellPermissionHandler.checkCanUse(cellCodeEnum);
        List<CellConfigDO> entities = list(new LambdaQueryWrapper<CellConfigDO>()
                .eq(CellConfigDO::getCellCode, cellCodeEnum)
                .eq(CellConfigDO::getIsUserVisible, true));
        return CellConfigConverter.INSTANCE.entityToVO(entities);
    }
}