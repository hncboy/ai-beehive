package cn.beehive.cell.core.hander;

import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.enums.CellStatusEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.cell.core.service.CellService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Objects;
import java.util.Optional;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 相关处理
 */
public class CellHandler {

    /**
     * 解析 cell code
     *
     * @param cellCodeStr cell code 字符串
     * @return cell code 枚举
     */
    public static CellCodeEnum parseCellCodeStr(String cellCodeStr) {
        return Optional.ofNullable(CellCodeEnum.CODE_MAP.get(cellCodeStr)).orElseThrow(() -> new ServiceException("图纸不存在"));
    }

    /**
     * 校验 cell 是否存在并可发布并返回
     *
     * @param cellCode cell code
     * @return cell
     */
    public static CellDO checkCellPublishExist(CellCodeEnum cellCode) {
        // TODO 缓存
        CellDO cellDO = SpringUtil.getBean(CellService.class)
                .getOne(new LambdaQueryWrapper<CellDO>().eq(CellDO::getCode, cellCode));
        if (Objects.isNull(cellDO)) {
            throw new ServiceException("图纸不存在");
        }
        if (cellDO.getStatus() != CellStatusEnum.PUBLISHED) {
            throw new ServiceException("该图纸未发布");
        }
        return cellDO;
    }
}
