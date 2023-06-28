package com.hncboy.beehive.cell.core.domain.query;

import com.hncboy.beehive.base.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间分页查询
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "房间分页查询")
public class RoomPageQuery extends PageQuery {

    @Schema(title = "名称")
    @Size(max = 10, message = "房间名称不能超过 10 个字")
    private String name;
}
