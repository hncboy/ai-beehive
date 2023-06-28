package com.hncboy.beehive.cell.core.hander.converter;

import com.hncboy.beehive.base.domain.entity.CellDO;
import com.hncboy.beehive.cell.core.domain.vo.CellImageVO;
import com.hncboy.beehive.cell.core.domain.vo.CellVO;
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
     * CellDO 转 CellVO
     *
     * @param cellDO CellDO
     * @return CellVO
     */
    CellVO entityToVO(CellDO cellDO);

    /**
     * List<CellDO> 转 List<CellImageVO>
     *
     * @param cellDOList List<CellDO>
     * @return List<CellImageVO>
     */
    List<CellImageVO> entityToImageVO(List<CellDO> cellDOList);
}
