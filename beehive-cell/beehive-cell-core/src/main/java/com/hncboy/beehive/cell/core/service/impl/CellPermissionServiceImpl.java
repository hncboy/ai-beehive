package com.hncboy.beehive.cell.core.service.impl;

import com.hncboy.beehive.base.domain.entity.CellPermissionDO;
import com.hncboy.beehive.base.mapper.CellPermissionMapper;
import com.hncboy.beehive.cell.core.service.CellPermissionService;
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
