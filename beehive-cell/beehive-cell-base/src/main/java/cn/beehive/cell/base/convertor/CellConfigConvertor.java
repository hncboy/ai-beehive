package cn.beehive.cell.base.convertor;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.handler.cellconfig.ICellConfigItem;
import cn.beehive.cell.base.domain.bo.SimpleCellConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 转换器，将其他对象转换为CellConfig对象
 *
 * @author CoDeleven
 */
@Mapper
public interface CellConfigConvertor {
    CellConfigConvertor INSTANCE = Mappers.getMapper(CellConfigConvertor.class);

    /**
     * 将 枚举配置项 转换为 具体的配置项 Entity
     *
     * @param cellConfigItem 枚举配置项
     * @return 具体的配置项 Entity
     */
    @Mapping(target = "keyName", expression = "java(cellConfigItem.getConfigKeyName())")
    @Mapping(target = "defaultValue", expression = "java(cellConfigItem.getDefaultValue())")
    @Mapping(target = "isRequired", expression = "java(cellConfigItem.isRequired())")
    @Mapping(target = "isUserVisible", expression = "java(cellConfigItem.isUserVisible())")
    @Mapping(target = "isUserModifiable", expression = "java(cellConfigItem.isUserModifiable())")
    @Mapping(target = "isUserLiveModifiable", expression = "java(cellConfigItem.isUserLiveModifiable())")
    @Mapping(target = "exampleValue", expression = "java(cellConfigItem.getExampleValue())")
    @Mapping(target = "introduce", expression = "java(cellConfigItem.getIntroduce())")
    @Mapping(target = "remark", expression = "java(cellConfigItem.getRemark())")
    @Mapping(target = "md5", expression = "java(cellConfigItem.getMD5())")
    @Mapping(target = "version", constant = "1")
    @Mapping(target = "isDelete", constant = "false")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    CellConfigDO convertFrom(Integer cellId, SimpleCellConfig cellConfigItem);

}
