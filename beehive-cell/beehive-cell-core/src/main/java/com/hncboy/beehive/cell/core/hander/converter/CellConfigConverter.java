package com.hncboy.beehive.cell.core.hander.converter;

import com.hncboy.beehive.base.domain.entity.CellConfigDO;
import com.hncboy.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import com.hncboy.beehive.cell.core.domain.vo.CellConfigVO;
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
     * List<CellConfigPermissionBO> 转 List<CellConfigVO>
     *
     * @param cellConfigPermissionBOList List<CellConfigPermissionBO>
     * @return List<CellConfigVO>
     */
    List<CellConfigVO> permissionBoToVo(List<CellConfigPermissionBO> cellConfigPermissionBOList);

    /**
     * List<CellConfigDO> 转 List<CellConfigPermissionBO>
     *
     * @param cellConfigDOList List<CellConfigDO>
     * @return List<CellConfigPermissionBO>
     */
    List<CellConfigPermissionBO> entityToPermissionBo(List<CellConfigDO> cellConfigDOList);

    /**
     * entityToPermissionBO 后置处理
     *
     * @param cellConfigDO           CellConfigDO
     * @param cellConfigPermissionBO CellConfigPermissionBO
     */
    @AfterMapping
    default void afterEntityToPermissionBo(CellConfigDO cellConfigDO, @MappingTarget CellConfigPermissionBO cellConfigPermissionBO) {
        cellConfigPermissionBO.setCellConfigCode(cellConfigDO.getCode());
    }

    /**
     * permissionBoToVo 后置处理
     *
     * @param cellConfigPermissionBO CellConfigPermissionBO
     * @param cellConfigVO           CellConfigVO
     */
    @AfterMapping
    default void afterPermissionBoToVo(CellConfigPermissionBO cellConfigPermissionBO, @MappingTarget CellConfigVO cellConfigVO) {
        // 不能使用默认值
        if (!cellConfigPermissionBO.getIsUserCanUseDefaultValue()) {
            // 默认值值置为空
            cellConfigVO.setDefaultValue(null);
            // 当作没有默认值
            cellConfigVO.setIsHaveDefaultValue(false);
        }

        // 默认值不可见
        if (!cellConfigPermissionBO.getIsUserValueVisible()) {
            // 默认值值置为空
            cellConfigVO.setDefaultValue(null);
        }
    }
}
