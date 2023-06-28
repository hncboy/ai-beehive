package com.hncboy.beehive.cell.core.domain.vo;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/6/18
 * Cell 封面展示对象
 */
@Data
@Schema(title = "Cell 封面展示对象")
public class CellImageVO {

    @Schema(title = "封面")
    private String imageUrl;

    @Schema(title = "编码")
    private CellCodeEnum code;
}
