package cn.beehive.cell.base.hander;

import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.base.enums.CellStatusEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.cell.base.service.CellService;
import cn.hutool.extra.spring.SpringUtil;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 相关处理
 */
public class CellHandler {

    /**
     * 校验 cell 是否存在并可发布并返回
     *
     * @param cellId cell id
     * @return cell
     */
    public static CellDO checkCellPublishExist(Integer cellId) {
        CellDO cellDO = SpringUtil.getBean(CellService.class).getById(cellId);
        if (Objects.isNull(cellDO)) {
            throw new ServiceException("图纸不存在");
        }
        if (cellDO.getStatus() != CellStatusEnum.PUBLISHED) {
            throw new ServiceException("该图纸未发布");
        }
        return cellDO;
    }
}
