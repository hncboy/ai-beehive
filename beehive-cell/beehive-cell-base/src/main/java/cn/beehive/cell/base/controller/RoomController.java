package cn.beehive.cell.base.controller;

import cn.beehive.base.handler.response.R;
import cn.beehive.cell.base.domain.query.RoomPageQuery;
import cn.beehive.cell.base.domain.request.RoomCreateRequest;
import cn.beehive.cell.base.domain.request.RoomEditRequest;
import cn.beehive.cell.base.domain.vo.RoomListVO;
import cn.beehive.cell.base.service.RoomService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hncboy
 * @date 2023/5/29
 * 房间控制器
 */
@AllArgsConstructor
@Tag(name = "房间相关接口")
@RequestMapping("/room")
@RestController
@Validated
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "房间分页列表")
    @GetMapping("/page")
    public R<IPage<RoomListVO>> pageRoom(@Validated RoomPageQuery roomPageQuery) {
        return R.data(roomService.pageRoom(roomPageQuery));
    }

    @Operation(summary = "创建房间")
    @PostMapping("/create")
    public R<RoomListVO> createRoom(@RequestBody @Validated RoomCreateRequest roomCreateRequest) {
        return R.data(roomService.createRoom(roomCreateRequest));
    }

    @Operation(summary = "固定房间")
    @GetMapping("/pin")
    public R<RoomListVO> pinRoom(@RequestParam Long roomId) {
        return R.data(roomService.pinRoom(roomId));
    }

    @Operation(summary = "编辑房间")
    @PostMapping("/edit")
    public R<RoomListVO> editRoom(@RequestBody @Validated RoomEditRequest roomEditRequest) {
        return R.data(roomService.editRoom(roomEditRequest));
    }

    @Operation(summary = "删除房间")
    @DeleteMapping("/delete")
    public R<Boolean> deleteRoom(@RequestParam Long roomId) {
        roomService.deleteRoom(roomId);
        return R.data(true);
    }
}