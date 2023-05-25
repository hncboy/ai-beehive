package cn.beehive.cell.base.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SimpleCellConfig 的组合类
 *
 * @author CoDeleven
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CompositeCellConfigListBO {
    /**
     * 该 cell 的名称，不允许修改
     */
    private String cellName;
    /**
     * 该 cell 的唯一标识，不允许修改
     */
    private String cellCode;
    /**
     * cell-config-init.yml 中每个配置项
     */
    private List<SimpleCellConfig> configItemList;
}
