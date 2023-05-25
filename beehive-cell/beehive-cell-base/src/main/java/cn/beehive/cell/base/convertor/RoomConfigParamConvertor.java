package cn.beehive.cell.base.convertor;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.base.domain.entity.RoomConfigParamDO;
import cn.beehive.base.handler.cellconfig.ICellConfigItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 转换器，将其他对象转换为 RoomConfigParam 对象
 *
 * @author CoDeleven
 */
@Mapper
public interface RoomConfigParamConvertor {
    RoomConfigParamConvertor INSTANCE = Mappers.getMapper(RoomConfigParamConvertor.class);

    /**
     * 将 枚举配置项 转换为 具体的配置项 Entity
     *
     * @param cellConfigItem 枚举配置项
     * @return 具体的配置项 Entity
     */
    @Mapping(target = "roomId", constant = "0")
    @Mapping(target = "configId", expression = "java(cellConfigItem.getId())")
    @Mapping(target = "userId", constant = "0")
    @Mapping(target = "value", expression = "java(cellConfigItem.getDefaultValue())")
    @Mapping(target = "isDelete", constant = "false")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    RoomConfigParamDO convertSystemParamFrom(CellConfigDO cellConfigItem);

}
