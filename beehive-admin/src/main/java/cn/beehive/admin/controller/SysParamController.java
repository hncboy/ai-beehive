package cn.beehive.admin.controller;

import cn.beehive.admin.domain.query.SysParamPageQuery;
import cn.beehive.admin.domain.request.SysParamRequest;
import cn.beehive.admin.domain.vo.SysParamVO;
import cn.beehive.admin.service.SysParamService;
import cn.beehive.base.annotation.ApiAdminRestController;
import cn.beehive.base.handler.response.R;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hncboy
 * @date 2023/5/10
 * 系统参数相关接口
 */
@AllArgsConstructor
@Tag(name = "管理端-系统参数相关接口")
@ApiAdminRestController("/sys_param")
public class SysParamController {

    private final SysParamService sysParamService;

    @GetMapping
    @Operation(summary = "参数分页")
    public R<IPage<SysParamVO>> page(@Validated SysParamPageQuery query) {
        return R.data(sysParamService.page(query));
    }

    @PostMapping
    @Operation(summary = "参数新增")
    public R<Integer> save(@Validated({SysParamRequest.Save.class}) @RequestBody SysParamRequest request) {
        return R.data(sysParamService.save(request));
    }

    @PutMapping
    @Operation(summary = "参数修改")
    public R<Boolean> update(@Validated({SysParamRequest.Update.class}) @RequestBody SysParamRequest request) {
        sysParamService.update(request);
        return R.data(true);
    }

    @DeleteMapping
    @Operation(summary = "参数删除")
    public R<Boolean> remove(@RequestParam Integer id) {
        sysParamService.remove(id);
        return R.data(true);
    }
}
