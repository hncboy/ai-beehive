package com.hncboy.beehive.cell.openai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.beehive.base.domain.entity.OpenAiApiKeyDO;
import com.hncboy.beehive.base.mapper.OpenAiApiKeyMapper;
import com.hncboy.beehive.cell.openai.service.OpenAiApiKeyService;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023/6/30
 * OpenAi ApiKey 业务实现类
 */
@Service
public class OpenAiApiKeyServiceImpl extends ServiceImpl<OpenAiApiKeyMapper, OpenAiApiKeyDO> implements OpenAiApiKeyService {
}
