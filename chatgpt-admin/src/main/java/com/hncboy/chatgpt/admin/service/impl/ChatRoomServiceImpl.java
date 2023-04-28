package com.hncboy.chatgpt.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.admin.domain.query.ChatRoomPageQuery;
import com.hncboy.chatgpt.admin.domain.vo.ChatRoomVO;
import com.hncboy.chatgpt.admin.handler.converter.ChatRoomConverter;
import com.hncboy.chatgpt.admin.service.ChatRoomService;
import com.hncboy.chatgpt.base.domain.entity.ChatRoomDO;
import com.hncboy.chatgpt.base.mapper.ChatRoomMapper;
import com.hncboy.chatgpt.base.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * @author hncboy
 * @date 2023-3-27
 * 聊天室业务实现类
 */
@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomDO> implements ChatRoomService {

    @Override
    public IPage<ChatRoomVO> pageChatRoom(ChatRoomPageQuery chatRoomPageQuery) {
        Page<ChatRoomDO> chatRoomPage = page(new Page<>(chatRoomPageQuery.getPageNum(), chatRoomPageQuery.getPageSize()), new LambdaQueryWrapper<ChatRoomDO>()
                .orderByDesc(ChatRoomDO::getId));

        return PageUtil.toPage(chatRoomPage, ChatRoomConverter.INSTANCE.entityToVO(chatRoomPage.getRecords()));
    }
}
