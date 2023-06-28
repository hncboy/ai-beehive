package com.hncboy.beehive.cell.core.cache;

import com.hncboy.beehive.base.domain.entity.CellDO;
import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.cell.core.service.CellService;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * @author hncboy
 * @date 2023/6/14
 * Cell 相关缓存
 */
public class CellCache {

    /**
     * 获取 cell 缓存
     * TODO 缓存补充
     *
     * @param cellCodeEnum cell 编码
     * @return cell 缓存
     */
    public static CellDO getCell(CellCodeEnum cellCodeEnum) {
        return SpringUtil.getBean(CellService.class)
                .getOne(new LambdaQueryWrapper<CellDO>().eq(CellDO::getCode, cellCodeEnum));
    }
}
