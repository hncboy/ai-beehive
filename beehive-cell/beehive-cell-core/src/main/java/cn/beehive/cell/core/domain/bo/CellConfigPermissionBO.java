package cn.beehive.cell.core.domain.bo;

import lombok.Data;

/**
 * @author hncboy
 * @date 2023/6/2
 * Cell 配置项权限业务参数
 */
@Data
public class CellConfigPermissionBO {

    /**
     * 名称
     */
    private String name;

    /**
     * code
     */
    private String code;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否必填，false 否 true 是
     */
    private Boolean isRequired;

    /**
     * 是否有默认值，false 否 true 是
     */
    private Boolean isHaveDefaultValue;

    /**
     * 用户是否可以使用默认值，false 否 true 是
     */
    private Boolean isUserCanUseDefaultValue;

    /**
     * 用户是否可见，false 否 true 是
     */
    private Boolean isUserVisible;

    /**
     * 用户是否可见默认值，false 否 true 是
     */
    private Boolean isUserValueVisible;

    /**
     * 用户是否可修改，false 否 true 是
     */
    private Boolean isUserModifiable;

    /**
     * 用户创建房间后是否可修改，false 否 true 是
     */
    private Boolean isUserLiveModifiable;
}
