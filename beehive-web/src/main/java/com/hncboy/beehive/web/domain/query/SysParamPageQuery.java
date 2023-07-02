package com.hncboy.beehive.web.domain.query;

import com.hncboy.beehive.base.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023/5/10
 * 系统参数分页查询
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "系统参数分页查询")
public class SysParamPageQuery extends PageQuery {

    @Schema(title = "名称")
    private String name;

    @Schema(title = "key")
    private String paramKey;

    @Schema(title = "value")
    private String paramValue;
}
