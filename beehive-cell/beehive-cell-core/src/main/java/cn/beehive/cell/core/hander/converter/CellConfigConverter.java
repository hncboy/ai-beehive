package cn.beehive.cell.core.hander.converter;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import cn.beehive.cell.core.domain.vo.CellConfigVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author CoDeleven
 * @date 2023/5/25
 * Cell 配置项相关转换
 */
@Mapper
public interface CellConfigConverter {

    CellConfigConverter INSTANCE = Mappers.getMapper(CellConfigConverter.class);

    /**
     * List<CellConfigDO> 转 List<CellConfigVO>
     *
     * @param cellConfigDOList List<CellConfigDO>
     * @return List<CellConfigVO>
     */
    List<CellConfigVO> entityToVO(List<CellConfigDO> cellConfigDOList);

    /**
     * List<CellConfigDO> 转 List<CellConfigPermissionBO>
     *
     * @param cellConfigDOList List<CellConfigDO>
     * @return List<CellConfigPermissionBO>
     */
    List<CellConfigPermissionBO> entityToPermissionBO(List<CellConfigDO> cellConfigDOList);

    /**
     * entityToVO 后置处理
     *
     * @param cellConfigDO CellConfigDO
     * @param cellConfigVO CellConfigVO
     */
    @AfterMapping
    default void afterEntityToVO(CellConfigDO cellConfigDO, @MappingTarget CellConfigVO cellConfigVO) {
        if (!cellConfigDO.getIsUserValueVisible() || !cellConfigDO.getIsUserCanUseDefaultValue()) {
            cellConfigVO.setDefaultValue(null);
        }
    }
}
