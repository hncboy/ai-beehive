package com.hncboy.chatgpt.front.domain.vo;

import com.hncboy.chatgpt.base.enums.ApiTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/3/22 20:48
 * API 模型信息
 */
@Data
@Schema(title = "API 模型信息")
public class ApiModelVO {

    @Schema(title = "是否开启鉴权")
    private Boolean auth;

    @Schema(title = "模型名称")
    private ApiTypeEnum model;
}
