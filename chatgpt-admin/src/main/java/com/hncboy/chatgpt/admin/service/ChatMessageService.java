package com.hncboy.chatgpt.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.admin.domain.query.ChatMessagePageQuery;
import com.hncboy.chatgpt.admin.domain.vo.ChatMessageVO;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;

/**
 * @author hncboy
 * @date 2023-3-27
 * 聊天记录相关业务接口
 */
public interface ChatMessageService extends IService<ChatMessageDO> {

    /**
     * 聊天记录分页
     *
     * @param chatMessagePageQuery 查询参数
     * @return 聊天记录展示参数
     */
    IPage<ChatMessageVO> pageChatMessage(ChatMessagePageQuery chatMessagePageQuery);
}
