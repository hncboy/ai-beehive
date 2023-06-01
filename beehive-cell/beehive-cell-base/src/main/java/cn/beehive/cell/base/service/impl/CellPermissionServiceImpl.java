package cn.beehive.cell.base.service.impl;

import cn.beehive.base.domain.entity.CellPermissionDO;
import cn.beehive.base.mapper.CellPermissionMapper;
import cn.beehive.cell.base.service.CellPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023/6/1
 * cell 权限业务实现类
 */
@Service
public class CellPermissionServiceImpl extends ServiceImpl<CellPermissionMapper, CellPermissionDO> implements CellPermissionService {
}
