package com.hncboy.chatgpt.base.util;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * @author hncboy
 * @date 2023-3-27
 * 分页工具
 */
@UtilityClass
public class PageUtil {

    /**
     * IPage 转 Page
     *
     * @param page   IPage
     * @param target 需要 copy 转换的类型
     * @param <T>    泛型
     * @return PageResult
     */
    public static <T> Page<T> toPage(IPage<?> page, Class<T> target) {
        return toPage(page, BeanUtil.copyToList(page.getRecords(), target));
    }

    /**
     * IPage 转 Page
     *
     * @param page    IPage
     * @param records 转换过的 List 模型
     * @param <T>     泛型
     * @return PageResult
     */
    public static <T> Page<T> toPage(IPage<?> page, List<T> records) {
        Page<T> pageResult = new Page<>();
        pageResult.setCurrent(page.getCurrent());
        pageResult.setSize(page.getSize());
        pageResult.setPages(page.getPages());
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(records);
        return pageResult;
    }
}
