package com.hncboy.beehive.web.handler.converter;

import com.hncboy.beehive.web.domain.vo.SysParamVO;
import com.hncboy.beehive.base.domain.entity.SysParamDO;
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
