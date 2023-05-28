package cn.beehive.cell.base.hander.converter;

import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.cell.base.domain.vo.CellVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author Codeleven
 * @date 2023/5/25
 * Cell 相关转换
 */
@Mapper
public interface CellConverter {

    CellConverter INSTANCE = Mappers.getMapper(CellConverter.class);

    /**
     * List<CellDO> 转 List<CellVO>
     *
     * @param cellDOList List<CellDO>
     * @return List<CellVO>
     */
    List<CellVO> entityToVO(List<CellDO> cellDOList);
}
