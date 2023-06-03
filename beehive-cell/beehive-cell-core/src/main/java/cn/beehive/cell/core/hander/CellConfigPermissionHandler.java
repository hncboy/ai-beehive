package cn.beehive.cell.core.hander;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.CellConfigPermissionDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.enums.CellConfigPermissionTypeEnum;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.cell.core.constant.CellPermissionConstant;
import cn.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import cn.beehive.cell.core.hander.converter.CellConfigConverter;
import cn.beehive.cell.core.service.CellConfigPermissionService;
import cn.beehive.cell.core.service.CellConfigService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/6/2
 * Cell 配置项权限处理器
 */
public class CellConfigPermissionHandler {

    /**
     * 根据 cellCode 获取配置项权限业务
     * TODO 从缓存中获取
     *
     * @param cellCode cellCode
     * @return cell 配置项权限业务
     */
    public static List<CellConfigPermissionBO> listCellConfigPermission(CellCodeEnum cellCode) {
        // 获取 Cell 配置项列表
        List<CellConfigDO> cellConfigDOList = SpringUtil.getBean(CellConfigService.class)
                .list(new LambdaQueryWrapper<CellConfigDO>().eq(CellConfigDO::getCellCode, cellCode));
        List<CellConfigPermissionBO> cellConfigPermissionBOList = CellConfigConverter.INSTANCE.entityToPermissionBO(cellConfigDOList);

        // 填充是否可以使用默认值
        populateIsCanUseDefaultValue(cellCode, cellConfigPermissionBOList);

        return cellConfigPermissionBOList;
    }

    /**
     * 填充是否可以使用默认值
     *
     * @param cellCode                   cellCode
     * @param cellConfigPermissionBOList cell 配置项权限业务列表
     */
    private static void populateIsCanUseDefaultValue(CellCodeEnum cellCode, List<CellConfigPermissionBO> cellConfigPermissionBOList) {
        // 过滤出无法使用默认值的配置项
        List<String> cannotUserDefaultValueCellConfigCodes = cellConfigPermissionBOList.stream()
                .filter(bo -> !bo.getIsUserCanUseDefaultValue())
                .map(CellConfigPermissionBO::getCellConfigCode)
                .collect(Collectors.toList());
        if (cannotUserDefaultValueCellConfigCodes.isEmpty()) {
            return;
        }
        // 添加任意配置项权限
        cannotUserDefaultValueCellConfigCodes.add(CellPermissionConstant.ANY_CELL_CONFIG_CODE);

        // 查询包含权限的记录
        CellConfigPermissionService cellConfigPermissionService = SpringUtil.getBean(CellConfigPermissionService.class);
        List<CellConfigPermissionDO> cellConfigPermissionDOList = cellConfigPermissionService.list(new LambdaQueryWrapper<CellConfigPermissionDO>()
                .select(CellConfigPermissionDO::getCellConfigCode)
                .eq(CellConfigPermissionDO::getType, CellConfigPermissionTypeEnum.CAN_USER_DEFAULT_VALUE)
                .eq(CellConfigPermissionDO::getCellCode, cellCode)
                .in(CellConfigPermissionDO::getCellConfigCode, cannotUserDefaultValueCellConfigCodes)
                .in(CellConfigPermissionDO::getUserId, CellPermissionConstant.ANY_USER_ID, FrontUserUtil.getUserId()));

        // 如果包含 cellConfigCode=0，表示所有 cellConfigCode 都可以使用默认值
        if (cellConfigPermissionDOList.stream().anyMatch(permission -> CellPermissionConstant.ANY_CELL_CONFIG_CODE.equals(permission.getCellConfigCode()))) {
            cellConfigPermissionBOList.forEach(bo -> bo.setIsUserCanUseDefaultValue(true));
            return;
        }

        // 如果包含指定的 cellConfigCode，则表示该 cellConfig 可以使用默认值
        for (CellConfigPermissionBO cellConfigPermissionBO : cellConfigPermissionBOList) {
            if (cellConfigPermissionDOList.stream().anyMatch(permission -> permission.getCellConfigCode().equals(cellConfigPermissionBO.getCellConfigCode()))) {
                cellConfigPermissionBO.setIsUserCanUseDefaultValue(true);
            }
        }
    }
}
