package com.hncboy.chatgpt.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hncboy.chatgpt.base.domain.entity.CurrentKeyDO;

/**
 * @author Jankin Wu
 * @description 针对表【current_key(为实现轮流给用户分配key，记录已使用的key)】的数据库操作Mapper
 * @createDate 2023-04-30 15:37:45
 * @Entity com.hncboy.chatgpt.base.domain.entity.CurrentKey
 */
public interface CurrentKeyMapper extends BaseMapper<CurrentKeyDO> {

}




