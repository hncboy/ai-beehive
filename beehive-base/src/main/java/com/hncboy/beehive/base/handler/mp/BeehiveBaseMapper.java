package com.hncboy.beehive.base.handler.mp;

import com.hncboy.beehive.base.domain.query.CursorQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/28
 * 自定义 BaseMapper
 */
public interface BeehiveBaseMapper<T> extends BaseMapper<T> {

    /**
     * 游标查询方法
     *
     * @param cursorQuery    游标查询对象
     * @param columnFunction 用于获取游标的字段
     * @param queryWrapper   查询条件
     * @return 查询结果列表
     */
    default List<T> cursorList(CursorQuery cursorQuery, SFunction<T, ?> columnFunction, @Param(Constants.WRAPPER) LambdaQueryWrapper<T> queryWrapper) {
        // 多少条记录
        queryWrapper.last("LIMIT " + cursorQuery.getSize());

        // 是否使用游标，第一条记录不会用到游标
        if (cursorQuery.getIsUseCursor()) {
            if (cursorQuery.getIsAsc()) {
                queryWrapper.gt(columnFunction, cursorQuery.getCursor());
            } else {
                queryWrapper.lt(columnFunction, cursorQuery.getCursor());
            }
        }
        // 排序
        queryWrapper.orderBy(true, cursorQuery.getIsAsc(), columnFunction);

        // 执行查询并返回结果
        return selectList(queryWrapper);
    }
}
