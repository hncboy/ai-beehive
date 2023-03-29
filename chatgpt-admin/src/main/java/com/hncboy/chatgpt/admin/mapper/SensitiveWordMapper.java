package com.hncboy.chatgpt.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hncboy.chatgpt.base.domain.entity.SensitiveWordDO;
import org.springframework.stereotype.Repository;

/**
 * @author hncboy
 * @date 2023/3/28 21:06
 * 敏感词数据库访问层
 */
@Repository("AdminSensitiveWordMapper")
public interface SensitiveWordMapper extends BaseMapper<SensitiveWordDO> {

}