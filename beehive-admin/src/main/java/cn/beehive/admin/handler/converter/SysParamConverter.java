package cn.beehive.admin.handler.converter;

import cn.beehive.admin.domain.vo.SysParamVO;
import cn.beehive.base.domain.entity.SysParamDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/10
 * 系统参数相关转换
 */
@Mapper
public interface SysParamConverter {

    SysParamConverter INSTANCE = Mappers.getMapper(SysParamConverter.class);

    /**
     * entity 转 VO
     *
     * @param sysParamDOList List<SysParamDO>
     * @return List<SysParamVO>
     */
    List<SysParamVO> entityToVO(List<SysParamDO> sysParamDOList);
}
