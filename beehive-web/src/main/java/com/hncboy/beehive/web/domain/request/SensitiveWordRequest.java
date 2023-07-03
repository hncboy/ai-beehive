package com.hncboy.beehive.web.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author hncboy
 * @date 2023/7/4
 * 敏感词新增或更新请求参数
 */
@Schema(title = "敏感词新增或更新请求参数")
@Data
public class SensitiveWordRequest {

    @NotNull(groups = {Update.class}, message = "id 不能为空")
    @Schema(title = "id，修改用")
    private Integer id;

    @NotEmpty(groups = {Save.class, Update.class}, message = "敏感词名称不能为空")
    @Size(min = 1, max = 50, groups = {Save.class, Update.class}, message = "名称字数范围[1,50]")
    @Schema(title = "名称")
    private String word;

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
