package cn.beehive.cell.core.service.impl;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.mapper.CellConfigMapper;
import cn.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import cn.beehive.cell.core.domain.vo.CellConfigVO;
import cn.beehive.cell.core.hander.CellConfigPermissionHandler;
import cn.beehive.cell.core.hander.CellHandler;
import cn.beehive.cell.core.hander.CellPermissionHandler;
import cn.beehive.cell.core.hander.converter.CellConfigConverter;
import cn.beehive.cell.core.service.CellConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置项业务接口实现类
 */
@Service
public class CellConfigServiceImpl extends ServiceImpl<CellConfigMapper, CellConfigDO> implements CellConfigService {

    @Override
    public List<CellConfigVO> listCellConfig(String cellCodeStr) {
        // 解析 cellCodeStr 为枚举
        CellCodeEnum cellCodeEnum = CellHandler.parseCellCodeStr(cellCodeStr);
        // 校验是否有使用权限
        CellPermissionHandler.checkCanUse(cellCodeEnum);
        // 根据配置项查询配置项权限参数列表
        List<CellConfigPermissionBO> cellConfigPermissionBOList = CellConfigPermissionHandler.listCellConfigPermission(cellCodeEnum);

        // 过滤不可见的配置项
        cellConfigPermissionBOList = cellConfigPermissionBOList.stream()
                .filter(CellConfigPermissionBO::getIsUserVisible)
                .collect(Collectors.toList());

        // 填充默认值信息，防止泄露
        CellConfigPermissionHandler.populateDefaultValue(cellConfigPermissionBOList);

        return CellConfigConverter.INSTANCE.permissionBOToVO(cellConfigPermissionBOList);
    }
}