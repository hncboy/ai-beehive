package com.hncboy.beehive.cell.core.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023/6/7
 * 房间配置项参数业务对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoomConfigParamBO extends CellConfigPermissionBO {

    /**
     * 最终的值
     */
    private String value;

    /**
     * 用户是否使用默认值，false 否 true 是
     */
    private Boolean isUseDefaultValue;
}
