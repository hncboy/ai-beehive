package cn.beehive.cell.base.hander;

import cn.beehive.base.domain.entity.CellPermissionDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.util.FrontUserUtil;
import cn.beehive.base.util.ThrowExceptionUtil;
import cn.beehive.cell.base.service.CellPermissionService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * @author hncboy
 * @date 2023/6/1
 * cell 权限相关处理
 */
public class CellPermissionHandler {

    /**
     * 代表任意 cell code
     */
    public static final String ANY_CELL_CODE = "0";

    /**
     * 代表任意用户 id
     */
    public static final Integer ANY_USER_ID = 0;

    /**
     * 检查是否可以使用
     *
     * @param cellCode cell code
     */
    public static void checkCanUse(CellCodeEnum cellCode) {
        // TODO 发送消息前应该也要校验，要不要加个注解
        // TODO 缓存
        CellPermissionService cellPermissionService = SpringUtil.getBean(CellPermissionService.class);
        long count = cellPermissionService.count(new LambdaQueryWrapper<CellPermissionDO>()
                // 任意用户或当前用户有该图纸权限或任意图纸权限
                .in(CellPermissionDO::getCellCode, cellCode, ANY_CELL_CODE)
                .in(CellPermissionDO::getUserId, FrontUserUtil.getUserId(), ANY_USER_ID));
        ThrowExceptionUtil.isTrue(count == 0).throwMessage("无权限使用该图纸，请联系管理员");
    }
}
