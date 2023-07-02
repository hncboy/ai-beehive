package com.hncboy.beehive.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hncboy.beehive.web.domain.query.SensitiveWordPageQuery;
import com.hncboy.beehive.web.domain.vo.SensitiveWordVO;
import com.hncboy.beehive.web.service.SensitiveWordService;
import com.hncboy.beehive.base.annotation.ApiAdminRestController;
import com.hncboy.beehive.base.handler.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

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
}
