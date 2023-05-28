package cn.beehive.cell.base.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 配置项展示对象
 */
@Data
@Schema(title = "Cell 配置项展示对象")
public class CellConfigVO {

    @Schema(title = "主键")
    private Integer id;

    @Schema(title = "cellId")
    private Integer cellId;

    @Schema(title = "名称")
    private String name;

    @Schema(title = "唯一编码，cell 中唯一")
    private String code;

    @Schema(title = "默认值")
    private String defaultValue;

    @Schema(title = "示例值")
    private String exampleValue;

    @Schema(title = "是否必填，false 否 true 是")
    private Boolean isRequired;

    @Schema(title = "用户是否可见，false 否 true 是")
    private Boolean isUserVisible;

    @Schema(title = "用户是否可修改，false 否 true 是")
    private Boolean isUserModifiable;

    @Schema(title = "用户创建房间后是否可修改，false 否 true 是")
    private Boolean isUserLiveModifiable;

    @Schema(title = "介绍")
    private String introduce;

    @Schema(title = "创建时间")
    private Date createTime;
}
