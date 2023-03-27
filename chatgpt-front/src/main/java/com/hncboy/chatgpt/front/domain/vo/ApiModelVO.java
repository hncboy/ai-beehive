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
@Schema(name = "API 模型信息")
public class ApiModelVO {

    @Schema(name = "是否开启鉴权")
    private Boolean auth;

    @Schema(name = "模型名称")
    private ApiTypeEnum model;
}
