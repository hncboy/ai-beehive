package cn.beehive.cell.base.service.impl;

import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.base.enums.CellStatusEnum;
import cn.beehive.base.mapper.CellMapper;
import cn.beehive.cell.base.domain.vo.CellVO;
import cn.beehive.cell.base.hander.converter.CellConverter;
import cn.beehive.cell.base.service.CellService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 业务接口实现类
 */
@Service
public class CellServiceImpl extends ServiceImpl<CellMapper, CellDO> implements CellService {

    @Override
    public List<CellVO> listCell() {
        List<CellDO> entities = list(new LambdaQueryWrapper<CellDO>()
                // 查询隐藏的
                .ne(CellDO::getStatus, CellStatusEnum.HIDDEN)
                // sort 降序
                .orderByDesc(CellDO::getSort));
        return CellConverter.INSTANCE.entityToVO(entities);
    }
}