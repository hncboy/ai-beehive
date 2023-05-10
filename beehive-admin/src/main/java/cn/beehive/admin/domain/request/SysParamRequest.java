package cn.beehive.admin.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(title = "id")
    private Integer id;

    @Size(min = 1, max = 50, groups = {Save.class, Update.class}, message = "名称字数范围[1,50]")
    private String name;

    @Size(min = 1, max = 50, groups = {Save.class, Update.class}, message = "key 字数范围[1,50]")
    private String key;

    @Size(max = 3000, groups = {Save.class, Update.class}, message = "value 不能超过 3000 字")
    private String value;

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
