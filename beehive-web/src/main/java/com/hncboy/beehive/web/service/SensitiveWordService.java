package com.hncboy.beehive.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.beehive.base.domain.entity.SensitiveWordDO;
import com.hncboy.beehive.web.domain.query.SensitiveWordPageQuery;
import com.hncboy.beehive.web.domain.request.SensitiveWordRequest;
import com.hncboy.beehive.web.domain.vo.SensitiveWordVO;

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

    /**
     * 新增敏感词
     *
     * @param sensitiveWordRequest 请求信息
     * @return 敏感词信息
     */
    SensitiveWordVO saveSensitiveWord(SensitiveWordRequest sensitiveWordRequest);

    /**
     * 删除敏感词
     *
     * @param id 主键
     */
    void deleteSensitiveWord(Integer id);

    /**
     * 启用敏感词
     *
     * @param id 主键
     * @return 敏感词信息
     */
    SensitiveWordVO enableSensitiveWord(Integer id);

    /**
     * 停用敏感词
     *
     * @param id 主键
     * @return 敏感词信息
     */
    SensitiveWordVO disableSensitiveWord(Integer id);

    /**
     * 修改敏感词
     *
     * @param sensitiveWordRequest 请求信息
     * @return 敏感词信息
     */
    SensitiveWordVO updateSensitiveWord(SensitiveWordRequest sensitiveWordRequest);
}
