package com.hncboy.beehive.web.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author hncboy
 * @date 2023/5/10
 * 系统参数
 */
@Data
@Schema(title = "系统参数")
public class SysParamVO {

    @Schema(title = "id")
    private Integer id;

    @Schema(title = "名称")
    private String name;

    @Schema(title = "key")
    private String paramKey;

    @Schema(title = "value")
    private String paramValue;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;
}
