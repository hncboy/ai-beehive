package com.hncboy.beehive.cell.core.hander;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hncboy.beehive.base.domain.entity.CellConfigDO;
import com.hncboy.beehive.base.domain.entity.CellConfigPermissionDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.enums.CellConfigPermissionTypeEnum;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.cell.core.cache.CellConfigCache;
import com.hncboy.beehive.cell.core.constant.CellPermissionConstant;
import com.hncboy.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import com.hncboy.beehive.cell.core.hander.converter.CellConfigConverter;
import com.hncboy.beehive.cell.core.service.CellConfigPermissionService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/6/2
 * Cell 配置项权限处理器
 */
public class CellConfigPermissionHandler {

    /**
     * 判断是否可以使用默认值
     *
     * @param cellConfigPermissionBO cell 配置项权限业务对象
     * @return 是否可以使用默认值
     */
    public static boolean isCanUseDefaultValue(CellConfigPermissionBO cellConfigPermissionBO) {
        if (!cellConfigPermissionBO.getIsHaveDefaultValue()) {
            return false;
        }
        return cellConfigPermissionBO.getIsUserCanUseDefaultValue();
    }

    /**
     * 根据 cellCodeEnum 获取配置项权限业务
     *
     * @param cellCodeEnum cellCodeEnum
     * @return cell 配置项权限业务
     */
    public static List<CellConfigPermissionBO> listCellConfigPermission(CellCodeEnum cellCodeEnum) {
        // 获取 Cell 配置项列表
        List<CellConfigDO> cellConfigDOList = CellConfigCache.listCellConfig(cellCodeEnum);
        List<CellConfigPermissionBO> cellConfigPermissionBOList = CellConfigConverter.INSTANCE.entityToPermissionBo(cellConfigDOList);

        // 填充是否可以使用默认值
        populateIsCanUseDefaultValue(cellCodeEnum, cellConfigPermissionBOList);

        return cellConfigPermissionBOList;
    }

    /**
     * 填充默认值信息，防止泄露
     *
     * @param cellConfigPermissionBOList cell 配置项权限业务列表
     */
    public static void populateDefaultValue(List<CellConfigPermissionBO> cellConfigPermissionBOList) {
        for (CellConfigPermissionBO cellConfigPermissionBO : cellConfigPermissionBOList) {
            // 可能存在用户用了自己的值，但是该配置被设置为不可见
            // 如果不可见 或 无法使用默认值，直接设置为 null
            if (!cellConfigPermissionBO.getIsUserVisible()
                    || !cellConfigPermissionBO.getIsUserValueVisible()
                    || !cellConfigPermissionBO.getIsUserCanUseDefaultValue()) {
                cellConfigPermissionBO.setDefaultValue(null);
            }
        }
    }

    /**
     * 填充是否可以使用默认值
     * TODO 取缓存
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
