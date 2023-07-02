package com.hncboy.beehive.web.service.impl;

import com.hncboy.beehive.web.domain.query.SysParamPageQuery;
import com.hncboy.beehive.web.domain.request.SysParamRequest;
import com.hncboy.beehive.web.domain.vo.SysParamVO;
import com.hncboy.beehive.web.handler.converter.SysParamConverter;
import com.hncboy.beehive.web.service.SysParamService;
import com.hncboy.beehive.base.cache.SysParamCache;
import com.hncboy.beehive.base.domain.entity.SysParamDO;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.mapper.SysParamMapper;
import com.hncboy.beehive.base.util.PageUtil;
import com.hncboy.beehive.base.util.ThrowExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/10
 * 系统参数业务实现类
 */
@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParamDO> implements SysParamService {

    @Override
    public IPage<SysParamVO> page(SysParamPageQuery query) {
        Page<SysParamDO> page = page(new Page<>(query.getPageNum(), query.getPageSize()), new LambdaQueryWrapper<SysParamDO>()
                .like(StrUtil.isNotBlank(query.getName()), SysParamDO::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getParamKey()), SysParamDO::getParamKey, query.getParamKey())
                .like(StrUtil.isNotBlank(query.getParamValue()), SysParamDO::getParamValue, query.getParamValue())
                .orderByDesc(SysParamDO::getUpdateTime));
        return PageUtil.toPage(page, SysParamConverter.INSTANCE.entityToVO(page.getRecords()));
    }

    @Override
    public Integer save(SysParamRequest request) {
        // 校验 key 是否唯一
        checkKeyUnique(request.getParamKey());

        // 保存系统参数
        SysParamDO sysParamDO = new SysParamDO();
        sysParamDO.setName(request.getName());
        sysParamDO.setParamKey(request.getParamKey());
        sysParamDO.setParamValue(request.getParamValue());
        sysParamDO.setIsDeleted(0);
        save(sysParamDO);

        // 保存缓存
        SysParamCache.setHashValue(request.getParamKey(), request.getParamValue());

        return sysParamDO.getId();
    }

    @Override
    public void update(SysParamRequest request) {
        SysParamDO sysParamDO = checkExist(request.getId());
        // key 改动的话需要校验是否重复
        if (ObjectUtil.notEqual(sysParamDO.getParamKey(), request.getParamKey())) {
            checkKeyUnique(request.getParamKey());
        }

        sysParamDO.setName(request.getName());
        sysParamDO.setParamKey(request.getParamKey());
        sysParamDO.setParamValue(request.getParamValue());
        updateById(sysParamDO);

        // 先删除再保存
        SysParamCache.deleteHashKey(request.getParamKey());
        SysParamCache.setHashValue(request.getParamKey(), request.getParamValue());
    }

    @Override
    public void remove(Integer id) {
        SysParamDO sysParamDO = checkExist(id);
        removeById(id);
        SysParamCache.deleteHashKey(sysParamDO.getParamKey());
    }

    /**
     * 校验 key 是否唯一
     *
     * @param key key
     */
    private void checkKeyUnique(String key) {
        long count = count(new LambdaQueryWrapper<SysParamDO>().eq(SysParamDO::getParamKey, key));
        ThrowExceptionUtil.isTrue(count > 0)
                .throwMessage(StrUtil.format("key [{}] 已存在", key));
    }

    /**
     * 校验参数是否存在
     *
     * @param id 主键
     */
    private SysParamDO checkExist(Integer id) {
        SysParamDO sysParamDO = getById(id);
        if (Objects.isNull(sysParamDO)) {
            throw new ServiceException(StrUtil.format("id [{}] 不存在", id));
        }
        return sysParamDO;
    }
}
