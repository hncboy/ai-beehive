package cn.beehive.cell.core.hander;

import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.base.enums.CellCodeEnum;
import cn.beehive.base.enums.CellStatusEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.cell.core.cache.CellCache;

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
        return Optional.ofNullable(CellCodeEnum.CODE_MAP.get(cellCodeStr.toLowerCase())).orElseThrow(() -> new ServiceException("图纸不存在"));
    }

    /**
     * 校验 cell 是否存在并可发布并返回
     *
     * @param cellCodeEnum cell code
     * @return cell
     */
    public static CellDO checkCellPublishExist(CellCodeEnum cellCodeEnum) {
        CellDO cellDO = getCell(cellCodeEnum);
        if (cellDO.getStatus() != CellStatusEnum.PUBLISHED) {
            throw new ServiceException("该图纸未发布");
        }
        return cellDO;
    }

    /**
     * 根据 cell code 获取 cell
     *
     * @param cellCodeEnum cell code
     * @return cell
     */
    public static CellDO getCell(CellCodeEnum cellCodeEnum) {
        CellDO cellDO = CellCache.getCell(cellCodeEnum);
        if (Objects.isNull(cellDO)) {
            throw new ServiceException("图纸不存在");
        }
        return cellDO;
    }
}
