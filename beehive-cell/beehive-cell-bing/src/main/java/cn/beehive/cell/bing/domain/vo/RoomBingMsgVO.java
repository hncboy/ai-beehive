package cn.beehive.cell.bing.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/26
 * Bing 房间消息展示参数
 */
@Data
@Schema(title = "Bing 房间消息展示参数")
public class RoomBingMsgVO {

    @Schema(title = "主键")
    private Long id;

}
