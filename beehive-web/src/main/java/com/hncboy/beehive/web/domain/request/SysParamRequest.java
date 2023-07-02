package com.hncboy.beehive.web.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/5/10
 * 系统参数新增或修改请求参数
 */
@Schema(title = "系统参数新增或修改请求参数")
@Data
public class SysParamRequest {

    @NotNull(groups = {Update.class}, message = "id 不能为空")
    @Schema(title = "id，修改用")
    private Integer id;

    @NotEmpty(groups = {Save.class, Update.class}, message = "paramKey不能为空")
    @Size(min = 1, max = 50, groups = {Save.class, Update.class}, message = "名称字数范围[1,50]")
    @Schema(title = "名称")
    private String name;

    @NotEmpty(groups = {Save.class, Update.class}, message = "paramKey 不能为空")
    @Size(min = 1, max = 50, groups = {Save.class, Update.class}, message = "paramKey 字数范围[1,50]")
    @Schema(title = "参数键")
    private String paramKey;

    @NotNull(groups = {Save.class, Update.class}, message = "paramValue 不能为空")
    @Size(max = 3000, groups = {Save.class, Update.class}, message = "paramValue 不能超过 3000 字")
    @Schema(title = "参数值")
    private String paramValue;

    /**
     * 新增
     */
    public interface Update {

    }

    /**
     * 更新
     */
    public interface Save {

    }
}
