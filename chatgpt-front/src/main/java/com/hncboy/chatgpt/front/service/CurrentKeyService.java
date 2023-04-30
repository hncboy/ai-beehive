package com.hncboy.chatgpt.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.domain.entity.CurrentKeyDO;

/**
 * @author Jankin Wu
 * @description 针对表【current_key(为实现轮流给用户分配key，记录已使用的key)】的数据库操作Service
 * @createDate 2023-04-30 15:37:45
 */
public interface CurrentKeyService extends IService<CurrentKeyDO> {

}
