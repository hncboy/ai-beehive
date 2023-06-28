package com.hncboy.beehive.base.handler.mp;

import com.hncboy.beehive.base.domain.query.CursorQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/28
 * 自定义 ServiceImpl
 */
public class BeehiveServiceImpl<M extends BeehiveBaseMapper<T>, T> extends ServiceImpl<M, T> implements IBeehiveService<T> {

    @Override
    public List<T> cursorList(CursorQuery cursorQuery, SFunction<T, ?> columnFunction, LambdaQueryWrapper<T> queryWrapper) {
        return baseMapper.cursorList(cursorQuery, columnFunction, queryWrapper);
    }
}
