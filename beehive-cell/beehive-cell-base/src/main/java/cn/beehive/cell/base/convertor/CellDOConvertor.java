package cn.beehive.cell.base.convertor;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.base.handler.cellconfig.ICellConfigItem;
import cn.beehive.cell.base.domain.bo.CompositeCellConfigListBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CellDOConvertor {
    CellDOConvertor INSTANCE = Mappers.getMapper(CellDOConvertor.class);

    /**
     * 根据 Cell 的基础配置 初始化一个 CellDO
     * 用于新建 CellDO
     *
     * @param compositeConfig Cell 配置
     * @return Cell对象
     */
    @Mapping(target = "code", expression = "java(compositeConfig.getCellCode())")
    @Mapping(target = "categoryId", constant = "0")
    @Mapping(target = "status", constant = "0")
    @Mapping(target = "weight", constant = "0")
    @Mapping(target = "imageUrl", constant = "default")
    @Mapping(target = "name", expression = "java(compositeConfig.getCellName())")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    CellDO convertFrom(CompositeCellConfigListBO compositeConfig);
}
