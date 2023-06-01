package cn.beehive.cell.core.service;

import cn.beehive.base.domain.entity.CellDO;
import cn.beehive.cell.core.domain.vo.CellVO;
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
}
