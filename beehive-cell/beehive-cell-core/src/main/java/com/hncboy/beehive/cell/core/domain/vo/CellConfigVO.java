package com.hncboy.beehive.cell.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置项展示对象
 */
@Data
@Schema(title = "Cell 配置项展示对象")
public class CellConfigVO {

    @Schema(title = "名称")
    private String name;

    @Schema(title = "配置项编码")
    private String cellConfigCode;

    @Schema(title = "默认值")
    private String defaultValue;

    @Schema(title = "示例值")
    private String exampleValue;

    @Schema(title = "是否必填，false 否 true 是")
    private Boolean isRequired;

    @Schema(title = "是否有默认值，false 否 true 是")
    private Boolean isHaveDefaultValue;

    @Schema(title = "用户是否可见默认值，false 否 true 是")
    private Boolean isUserValueVisible;

    @Schema(title = "用户是否可修改，false 否 true 是")
    private Boolean isUserModifiable;

    @Schema(title = "用户创建房间后是否可修改，false 否 true 是")
    private Boolean isUserLiveModifiable;

    @Schema(title = "介绍")
    private String introduce;

    @Schema(title = "前端组件类型")
    private String frontComponentType;

    @Schema(title = "前端組件内容")
    private String frontComponentContent;
}
