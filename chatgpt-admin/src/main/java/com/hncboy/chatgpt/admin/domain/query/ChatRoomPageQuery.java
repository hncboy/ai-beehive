package com.hncboy.chatgpt.admin.domain.query;

import com.hncboy.chatgpt.base.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hncboy
 * @date 2023-3-27
 * 聊天记录分页查询
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "聊天室分页查询")
public class ChatRoomPageQuery extends PageQuery {
}
