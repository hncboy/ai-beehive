package com.hncboy.chatgpt.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import org.springframework.stereotype.Repository;

/**
 * @author hncboy
 * @date 2023/3/27 21:43
 * 聊天记录数据访问层
 */
@Repository("AdminChatMessageMapper")
public interface ChatMessageMapper extends BaseMapper<ChatMessageDO> {
}
