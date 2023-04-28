package com.hncboy.chatgpt.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.admin.domain.query.SensitiveWordPageQuery;
import com.hncboy.chatgpt.admin.domain.vo.SensitiveWordVO;
import com.hncboy.chatgpt.base.domain.entity.SensitiveWordDO;

/**
 * @author hncboy
 * @date 2023-3-28
 * 敏感词业务接口
 */
public interface SensitiveWordService extends IService<SensitiveWordDO> {

    /**
     * 敏感词分页查询
     *
     * @param sensitiveWordPageQuery 查询条件
     * @return 敏感词分页列表
     */
    IPage<SensitiveWordVO> pageSensitiveWord(SensitiveWordPageQuery sensitiveWordPageQuery);
}
