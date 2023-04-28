package com.hncboy.chatgpt.admin.controller;

import com.hncboy.chatgpt.admin.domain.vo.RateLimitVO;
import com.hncboy.chatgpt.admin.service.RateLimitService;
import com.hncboy.chatgpt.base.annotation.ApiAdminRestController;
import com.hncboy.chatgpt.base.handler.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author hncboy
 * @date 2023-4-1
 * 限流记录相关接口
 */
@AllArgsConstructor
@Tag(name = "管理端-限流记录相关接口")
@ApiAdminRestController("/rate_limit")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    @Operation(summary = "限流列表")
    @GetMapping("/list")
    public R<List<RateLimitVO>> listRateLimit() {
        return R.data(rateLimitService.listRateLimit());
    }
}
