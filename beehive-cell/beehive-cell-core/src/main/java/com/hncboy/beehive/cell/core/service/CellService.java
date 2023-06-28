package com.hncboy.beehive.cell.core.service;

import com.hncboy.beehive.base.domain.entity.CellDO;
import com.hncboy.beehive.cell.core.domain.vo.CellImageVO;
import com.hncboy.beehive.cell.core.domain.vo.CellVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 业务接口
 */
public interface CellService extends IService<CellDO> {

    /**
     * cell 列表
     *
     * @return cell 列表
     */
    List<CellVO> listCell();

    /**
     * cell 封面列表
     *
     * @return cell 封面列表
     */
    List<CellImageVO> listCellImage();
}
