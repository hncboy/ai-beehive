package com.hncboy.chatgpt.base.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/3/27 23:28
 * 分页工具
 */
@UtilityClass
public class PageUtil {

    /**
     * IPage 转 Page
     *
     * @param page    IPage
     * @param records 转换过的list模型
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
