package cn.beehive.cell.core.hander;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.hutool.core.util.StrUtil;

/**
 * @author hncboy
 * @date 2023/6/2
 * Cell 配置项处理器
 */
public class CellConfigHandler {

    /**
     * 校验配置项是否配置合理
     *
     * @param cellConfigDO 配置项
     */
    public static void validateReasonable(CellConfigDO cellConfigDO) {
        // 如果必填，但是没有默认值
        if (cellConfigDO.getIsRequired() && !cellConfigDO.getIsHaveDefaultValue()) {
            if (!cellConfigDO.getIsUserValueVisible()) {
                throw new RuntimeException(StrUtil.format("配置项 {} 配置不合理：必填无默认值用户不可见"));
            }
        }
        // TODO 其他
    }
}
