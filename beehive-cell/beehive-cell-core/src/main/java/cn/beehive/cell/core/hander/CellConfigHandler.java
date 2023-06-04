package cn.beehive.cell.core.hander;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.cell.core.service.CellConfigService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/6/2
 * Cell 配置项处理器
 */
public class CellConfigHandler {

    /**
     * 获取配置项 Map
     *
     * @param cellCodeEnum cellCodeEnum
     * @return 配置项 Map
     */
    public static Map<String, CellConfigDO> getCellConfigMap(CellCodeEnum cellCodeEnum) {
        // TODO 缓存
        CellConfigService cellConfigService = SpringUtil.getBean(CellConfigService.class);
        List<CellConfigDO> cellConfigDOList = cellConfigService.list(new LambdaQueryWrapper<CellConfigDO>().eq(CellConfigDO::getCellCode, cellCodeEnum));
        // 转为 Map
        return cellConfigDOList.stream().collect(Collectors.toMap(CellConfigDO::getCode, Function.identity()));
    }

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
