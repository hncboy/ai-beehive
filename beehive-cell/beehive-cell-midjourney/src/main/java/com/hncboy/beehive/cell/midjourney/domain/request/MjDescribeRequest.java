package com.hncboy.beehive.cell.midjourney.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hncboy
 * @date 2023/5/18
 * Midjourney 描述请求
 */
@Data
@Schema(title = "Midjourney 描述请求")
public class MjDescribeRequest {

    @NotNull(message = "房间 id 不能为空")
    @Schema(title = "房间 id")
    private Long roomId;

    @NotNull(message = "图片文件不能为空")
    @Schema(title = "图片文件")
    private MultipartFile file;
}
