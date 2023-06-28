package com.hncboy.beehive.cell.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023/6/3
 * 房间配置参数展示对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "房间配置参数展示对象")
public class RoomConfigParamVO extends CellConfigVO {

    @Schema(title = "用户使用的值：默认值或用户自己填的值")
    private String value;

    @Schema(title = "用户是否使用默认值，false 否 true 是")
    private Boolean isUseDefaultValue;
}
