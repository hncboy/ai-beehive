package cn.beehive.cell.base.domain.bo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简单的 CellConfig 包裹类
 * 是 CellConfigDO 的简化版，用于承载 Yaml 中加载出来的配置内容
 *
 * @author CoDeleven
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleCellConfig {
    /**
     * 配置项名称
     */
    private String keyName;

    /**
     * 该配置项的默认值
     */
    private String defaultValue;

    /**
     * 是否必填，0非必填，1必填
     */
    private Boolean isRequired;

    /**
     * 用户是否可见，0不可见，1可见
     */
    private Boolean isUserVisible;

    /**
     * 用户是否可修改，0不可以，1可以
     */
    private Boolean isUserModifiable;

    /**
     * 用户是否在创建后可修改，0不可以，1可以
     */
    private Boolean isUserLiveModifiable;

    /**
     * 示例值
     */
    private String exampleValue;

    /**
     * 配置项介绍
     */
    private String introduce;

    /**
     * 备注，管理端查看
     */
    private String remark;
}
