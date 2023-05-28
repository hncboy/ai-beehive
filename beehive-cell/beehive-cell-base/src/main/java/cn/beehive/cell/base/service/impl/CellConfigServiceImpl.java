package cn.beehive.cell.base.service.impl;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.mapper.CellConfigMapper;
import cn.beehive.cell.base.domain.vo.CellConfigVO;
import cn.beehive.cell.base.hander.converter.CellConfigConverter;
import cn.beehive.cell.base.service.CellConfigService;
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
    public List<CellConfigVO> listCellConfig(Integer cellId) {
        List<CellConfigDO> entities = list(new LambdaQueryWrapper<CellConfigDO>()
                .eq(CellConfigDO::getIsUserVisible, true));
        return CellConfigConverter.INSTANCE.entityToVO(entities);
    }
}