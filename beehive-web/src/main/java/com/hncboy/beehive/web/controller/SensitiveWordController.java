package com.hncboy.beehive.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hncboy.beehive.base.annotation.ApiAdminRestController;
import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.web.domain.query.SensitiveWordPageQuery;
import com.hncboy.beehive.web.domain.request.SensitiveWordRequest;
import com.hncboy.beehive.web.domain.vo.SensitiveWordVO;
import com.hncboy.beehive.web.service.SensitiveWordService;
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
 * @date 2023-3-28
 * 敏感词相关接口
 */
@AllArgsConstructor
@Tag(name = "管理端-敏感词相关接口")
@ApiAdminRestController("/sensitive_word")
public class SensitiveWordController {

    private final SensitiveWordService sensitiveWordService;

    @GetMapping("/page")
    @Operation(summary = "敏感词列表分页")
    public R<IPage<SensitiveWordVO>> page(@Validated SensitiveWordPageQuery sensitiveWordPageQuery) {
        return R.data(sensitiveWordService.pageSensitiveWord(sensitiveWordPageQuery));
    }

    @PostMapping("/save")
    @Operation(summary = "新增敏感词")
    public R<SensitiveWordVO> save(@Validated({SensitiveWordRequest.Save.class}) @RequestBody SensitiveWordRequest sensitiveWordRequest) {
        return R.data(sensitiveWordService.saveSensitiveWord(sensitiveWordRequest));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除敏感词")
    public R<Void> delete(@RequestParam Integer id) {
        sensitiveWordService.deleteSensitiveWord(id);
        return R.success("删除成功");
    }

    @PutMapping("/enable")
    @Operation(summary = "启用敏感词")
    public R<SensitiveWordVO> enable(@RequestParam Integer id) {
        return R.data(sensitiveWordService.enableSensitiveWord(id));
    }

    @PutMapping("/disable")
    @Operation(summary = "停用敏感词")
    public R<SensitiveWordVO> disable(@RequestParam Integer id) {
        return R.data(sensitiveWordService.disableSensitiveWord(id));
    }

    @PutMapping("/update")
    @Operation(summary = "修改敏感词")
    public R<SensitiveWordVO> update(@Validated({SensitiveWordRequest.Update.class}) @RequestBody SensitiveWordRequest sensitiveWordRequest) {
        return R.data(sensitiveWordService.updateSensitiveWord(sensitiveWordRequest));
    }
}
