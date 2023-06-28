package com.hncboy.beehive.cell.core.controller;

import com.hncboy.beehive.base.handler.response.R;
import com.hncboy.beehive.cell.core.domain.vo.CellImageVO;
import com.hncboy.beehive.cell.core.domain.vo.CellVO;
import com.hncboy.beehive.cell.core.service.CellService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hncboy
 * @date 2023/5/29
 * Cell 控制器
 */
@AllArgsConstructor
@Tag(name = "Cell 相关接口")
@RequestMapping("/cell")
@RestController
public class CellController {

    private final CellService cellService;

    @Operation(summary = "cell 列表")
    @GetMapping("/list")
    public R<List<CellVO>> listCell() {
        return R.data(cellService.listCell());
    }

    @Operation(summary = "cell 封面列表")
    @GetMapping("/list_image")
    public R<List<CellImageVO>> listCellImage() {
        return R.data(cellService.listCellImage());
    }
}
