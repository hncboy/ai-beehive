package cn.beehive.cell.midjourney.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 描述请求
 */
@Data
@Schema(title = "Midjourney 描述请求")
public class MjDescribeRequest {

    @Schema(title = "文件 base64: data:image/png;base64,xxx")
    private String base64;
}
