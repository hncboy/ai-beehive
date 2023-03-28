package com.hncboy.chatgpt.admin.domain.query;

import com.hncboy.chatgpt.base.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023/3/27 23:14
 * 聊天记录分页查询
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(name = "聊天记录分页查询")
public class ChatMessagePageQuery extends PageQuery {

    @Schema(title = "聊天室 id")
    private Long chatRoomId;
}
