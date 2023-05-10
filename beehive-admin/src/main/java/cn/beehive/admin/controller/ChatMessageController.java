package cn.beehive.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.beehive.admin.domain.query.ChatMessagePageQuery;
import cn.beehive.admin.domain.vo.ChatMessageVO;
import cn.beehive.admin.service.ChatMessageService;
import cn.beehive.base.annotation.ApiAdminRestController;
import cn.beehive.base.handler.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.annotation.Resource;

/**
 * @author hncboy
 * @date 2023-3-27
 * 聊天记录相关接口
 */
@AllArgsConstructor
@Tag(name = "管理端-聊天记录相关接口")
@ApiAdminRestController("/chat_message")
public class ChatMessageController {

    @Resource
    private final ChatMessageService chatMessageService;

    @Operation(summary = "分页列表")
    @PostMapping("/page")
    public R<IPage<ChatMessageVO>> page(@Validated @RequestBody ChatMessagePageQuery chatMessagePageQuery) {
        return R.data(chatMessageService.pageChatMessage(chatMessagePageQuery));
    }
}
