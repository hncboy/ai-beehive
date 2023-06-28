package com.hncboy.beehive.cell.core.hander;

import com.hncboy.beehive.base.domain.entity.CellPermissionDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.util.FrontUserUtil;
import com.hncboy.beehive.base.util.ThrowExceptionUtil;
import com.hncboy.beehive.cell.core.constant.CellPermissionConstant;
import com.hncboy.beehive.cell.core.service.CellPermissionService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * @author hncboy
 * @date 2023/6/1
 * cell 权限相关处理
 */
public class CellPermissionHandler {

    /**
     * 检查是否可以使用
     *
     * @param cellCodeEnum cell code
     */
    public static void checkCanUse(CellCodeEnum cellCodeEnum) {
        // 校验是否发布
        CellHandler.checkCellPublishExist(cellCodeEnum);

        // TODO 缓存

        // 判断是否有图纸使用权限
        CellPermissionService cellPermissionService = SpringUtil.getBean(CellPermissionService.class);
        long count = cellPermissionService.count(new LambdaQueryWrapper<CellPermissionDO>()
                // 任意用户或当前用户有该图纸权限或任意图纸权限
                .in(CellPermissionDO::getCellCode, cellCodeEnum, CellPermissionConstant.ANY_CELL_CODE)
                .in(CellPermissionDO::getUserId, FrontUserUtil.getUserId(), CellPermissionConstant.ANY_USER_ID));
        ThrowExceptionUtil.isTrue(count == 0).throwMessage("无权限使用该图纸，请联系管理员");
    }
}
