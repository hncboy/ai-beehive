package com.hncboy.beehive.cell.core.controller;

import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.core.domain.request.RoomConfigParamEditRequest;
import com.hncboy.beehive.cell.core.domain.vo.RoomConfigParamVO;
import com.hncboy.beehive.cell.core.service.RoomConfigParamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/6/3
 * 房间配置项参数控制器
 */
@AllArgsConstructor
@Tag(name = "房间配置项参数相关接口")
@RequestMapping("/room_config_param")
@RestController
public class RoomConfigParamController {

    private final RoomConfigParamService roomConfigParamService;

    @Operation(summary = "参数列表")
    @GetMapping("/list")
    public R<List<RoomConfigParamVO>> list(@RequestParam Long roomId) {
        return R.data(roomConfigParamService.list(roomId));
    }

    @Operation(summary = "编辑参数")
    @PostMapping("/edit")
    public R<List<RoomConfigParamVO>> edit(@Validated @RequestBody RoomConfigParamEditRequest request) {
        return R.data(roomConfigParamService.edit(request));
    }
}
