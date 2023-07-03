package com.hncboy.beehive.web.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.beehive.base.domain.entity.SensitiveWordDO;
import com.hncboy.beehive.base.enums.EnableDisableStatusEnum;
import com.hncboy.beehive.base.handler.SensitiveWordHandler;
import com.hncboy.beehive.base.mapper.SensitiveWordMapper;
import com.hncboy.beehive.base.util.PageUtil;
import com.hncboy.beehive.base.util.ThrowExceptionUtil;
import com.hncboy.beehive.web.domain.query.SensitiveWordPageQuery;
import com.hncboy.beehive.web.domain.request.SensitiveWordRequest;
import com.hncboy.beehive.web.domain.vo.SensitiveWordVO;
import com.hncboy.beehive.web.handler.converter.SensitiveWordConverter;
import com.hncboy.beehive.web.service.SensitiveWordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-28
 * 敏感词业务实现类
 */
@Service
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWordDO> implements SensitiveWordService {

    @Override
    public IPage<SensitiveWordVO> pageSensitiveWord(SensitiveWordPageQuery sensitiveWordPageQuery) {
        IPage<SensitiveWordDO> sensitiveWordPage = page(new Page<>(sensitiveWordPageQuery.getPageNum(), sensitiveWordPageQuery.getPageSize()),
                new LambdaQueryWrapper<SensitiveWordDO>()
                        .eq(Objects.nonNull(sensitiveWordPageQuery.getStatus()), SensitiveWordDO::getStatus, sensitiveWordPageQuery.getStatus())
                        .like(StrUtil.isNotBlank(sensitiveWordPageQuery.getWord()), SensitiveWordDO::getWord, sensitiveWordPageQuery.getWord())
                        .orderByDesc(SensitiveWordDO::getCreateTime));

        return PageUtil.toPage(sensitiveWordPage, SensitiveWordConverter.INSTANCE.entityToVO(sensitiveWordPage.getRecords()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SensitiveWordVO saveSensitiveWord(SensitiveWordRequest sensitiveWordRequest) {
        // 校验敏感词是否唯一
        checkSensitiveWordUnique(sensitiveWordRequest.getWord());

        SensitiveWordDO sensitiveWordDO = new SensitiveWordDO();
        sensitiveWordDO.setWord(sensitiveWordRequest.getWord());
        sensitiveWordDO.setStatus(EnableDisableStatusEnum.ENABLE);
        save(sensitiveWordDO);

        // 清除敏感词树
        SensitiveWordHandler.clearWordTree();

        return SensitiveWordConverter.INSTANCE.entityToVO(sensitiveWordDO);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSensitiveWord(Integer id) {
        removeById(id);
        // 清除敏感词树
        SensitiveWordHandler.clearWordTree();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SensitiveWordVO enableSensitiveWord(Integer id) {
        boolean update = update(new SensitiveWordDO(), new LambdaUpdateWrapper<SensitiveWordDO>()
                .set(SensitiveWordDO::getStatus, EnableDisableStatusEnum.ENABLE)
                .eq(SensitiveWordDO::getId, id)
                .eq(SensitiveWordDO::getStatus, EnableDisableStatusEnum.DISABLE));
        ThrowExceptionUtil.isFalse(update).throwMessage("敏感词启用失败");

        // 清除敏感词树
        SensitiveWordHandler.clearWordTree();

        return SensitiveWordConverter.INSTANCE.entityToVO(getById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SensitiveWordVO disableSensitiveWord(Integer id) {
        boolean update = update(new SensitiveWordDO(), new LambdaUpdateWrapper<SensitiveWordDO>()
                .set(SensitiveWordDO::getStatus, EnableDisableStatusEnum.DISABLE)
                .eq(SensitiveWordDO::getId, id)
                .eq(SensitiveWordDO::getStatus, EnableDisableStatusEnum.ENABLE));
        ThrowExceptionUtil.isFalse(update).throwMessage("敏感词停用失败");

        // 清除敏感词树
        SensitiveWordHandler.clearWordTree();

        return SensitiveWordConverter.INSTANCE.entityToVO(getById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public SensitiveWordVO updateSensitiveWord(SensitiveWordRequest sensitiveWordRequest) {
        SensitiveWordDO sensitiveWordDO = checkExist(sensitiveWordRequest.getId());

        if (ObjectUtil.notEqual(sensitiveWordDO.getWord(), sensitiveWordRequest.getWord())) {
            // 校验敏感词是否唯一
            checkSensitiveWordUnique(sensitiveWordRequest.getWord());
        }

        sensitiveWordDO.setWord(sensitiveWordRequest.getWord());
        updateById(sensitiveWordDO);

        // 清除敏感词树
        SensitiveWordHandler.clearWordTree();

        return SensitiveWordConverter.INSTANCE.entityToVO(sensitiveWordDO);
    }

    /**
     * 校验敏感词名称是否唯一
     *
     * @param word 敏感词
     */
    private void checkSensitiveWordUnique(String word) {
        long existCount = count(new LambdaQueryWrapper<SensitiveWordDO>()
                .eq(SensitiveWordDO::getWord, word));
        ThrowExceptionUtil.isTrue(existCount > 0).throwMessage("该敏感词名称已存在");
    }

    /**
     * 检查敏感词是否存在并返回
     *
     * @param id 主键
     * @return 敏感词实体
     */
    private SensitiveWordDO checkExist(Integer id) {
        SensitiveWordDO sensitiveWordDO = getById(id);
        ThrowExceptionUtil.isTrue(Objects.isNull(sensitiveWordDO)).throwMessage("敏感词不存在");
        return sensitiveWordDO;
    }
}
