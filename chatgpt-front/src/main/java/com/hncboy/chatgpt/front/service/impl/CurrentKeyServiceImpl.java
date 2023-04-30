package com.hncboy.chatgpt.front.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.CurrentKeyDO;
import com.hncboy.chatgpt.base.mapper.CurrentKeyMapper;
import com.hncboy.chatgpt.front.service.CurrentKeyService;
import org.springframework.stereotype.Service;

/**
 * @author Jankin Wu
 * @description 针对表【current_key(为实现轮流给用户分配key，记录已使用的key)】的数据库操作Service实现
 * @createDate 2023-04-30 15:37:45
 */
@Service
public class CurrentKeyServiceImpl extends ServiceImpl<CurrentKeyMapper, CurrentKeyDO>
        implements CurrentKeyService {

}




