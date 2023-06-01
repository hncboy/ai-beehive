package cn.beehive.cell.core.service;

import cn.beehive.base.domain.entity.CellConfigDO;
import cn.beehive.cell.core.domain.vo.CellConfigVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置项业务接口
 */
public interface CellConfigService extends IService<CellConfigDO> {

    /**
     * cell 配置项列表
     *
     * @param cellCode cellCode
     * @return 列表
     */
    List<CellConfigVO> listCellConfig(String cellCode);
}
