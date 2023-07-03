package com.hncboy.beehive.web.handler.converter;

import com.hncboy.beehive.base.domain.entity.SensitiveWordDO;
import com.hncboy.beehive.web.domain.vo.SensitiveWordVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hncboy
 * @date 2023-3-28
 * 敏感词相关转换
 */
@Mapper
public interface SensitiveWordConverter {

    SensitiveWordConverter INSTANCE = Mappers.getMapper(SensitiveWordConverter.class);

    /**
     * List<SensitiveWordDO> 转 List<SensitiveWordVO>
     *
     * @param sensitiveWordDOList List<SensitiveWordDO>
     * @return List<SensitiveWordVO>
     */
    List<SensitiveWordVO> entityToVO(List<SensitiveWordDO> sensitiveWordDOList);

    /**
     * SensitiveWordDO 转 SensitiveWordVO
     *
     * @param sensitiveWordDO SensitiveWordDO
     * @return SensitiveWordVO
     */
    SensitiveWordVO entityToVO(SensitiveWordDO sensitiveWordDO);
}
