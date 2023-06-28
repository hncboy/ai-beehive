package com.hncboy.beehive.cell.core.domain.vo;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import com.hncboy.beehive.base.enums.CellStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 展示对象
 */
@Data
@Schema(title = "Cell 展示对象")
public class CellVO {

    @Schema(title = "主键")
    private Integer id;

    @Schema(title = "名称")
    private String name;

    @Schema(title = "封面")
    private String imageUrl;

    @Schema(title = "唯一编码")
    private CellCodeEnum code;

    @Schema(title = "状态")
    private CellStatusEnum status;

    @Schema(title = "是否能使用，false 否 true 是")
    private Boolean isCanUse;

    @Schema(title = "排序，值大的排前面")
    private Integer sort;

    @Schema(title = "介绍")
    private String introduce;

    @Schema(title = "创建时间")
    private Date createTime;
}
