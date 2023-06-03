package cn.beehive.cell.core.hander.converter;

import cn.beehive.cell.core.domain.bo.CellConfigPermissionBO;
import cn.beehive.cell.core.domain.vo.RoomConfigParamVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author hncboy
 * @date 2023/6/3
 * 房间配置参数相关转换
 */
@Mapper
public interface RoomConfigParamConverter {

    RoomConfigParamConverter INSTANCE = Mappers.getMapper(RoomConfigParamConverter.class);

    /**
     * CellConfigPermissionBO 转 RoomConfigParamVO
     *
     * @param cellConfigPermissionBO CellConfigPermissionBO
     * @return RoomConfigParamVO
     */
    RoomConfigParamVO cellConfigPermissionBOToVO(CellConfigPermissionBO cellConfigPermissionBO);
}
