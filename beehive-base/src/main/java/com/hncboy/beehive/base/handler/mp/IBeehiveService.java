package com.hncboy.beehive.base.handler.mp;

import com.hncboy.beehive.base.domain.query.CursorQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/28
 * 自定义 IService
 */
public interface IBeehiveService<T> extends IService<T> {

    /**
     * 游标查询方法
     *
     * @param cursorQuery    游标查询对象
     * @param columnFunction 用于获取游标的字段
     * @param queryWrapper   查询条件
     * @return 查询结果列表
     */
    List<T> cursorList(CursorQuery cursorQuery, SFunction<T, ?> columnFunction, LambdaQueryWrapper<T> queryWrapper);
}
