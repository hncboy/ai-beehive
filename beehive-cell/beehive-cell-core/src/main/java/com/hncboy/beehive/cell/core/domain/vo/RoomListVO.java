package com.hncboy.beehive.cell.core.domain.vo;

import com.hncboy.beehive.base.enums.CellCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间列表展示对象
 */
@Data
@Schema(title = "房间列表展示对象")
public class RoomListVO {

    @Schema(title = "房间 id")
    private Long roomId;

    @Schema(title = "颜色，十六进制")
    private String color;

    @Schema(title = "名称")
    private String name;

    @Schema(title = "是否固定 false 否 true 是")
    private Boolean isPinned;

    @Schema(title = "cell code")
    private CellCodeEnum cellCode;

    @Schema(title = "创建时间")
    private Date createTime;
}
