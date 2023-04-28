package com.hncboy.chatgpt.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.admin.domain.query.ChatMessagePageQuery;
import com.hncboy.chatgpt.admin.domain.vo.ChatMessageVO;
import com.hncboy.chatgpt.admin.handler.converter.ChatMessageConverter;
import com.hncboy.chatgpt.admin.service.ChatMessageService;
import com.hncboy.chatgpt.base.domain.entity.ChatMessageDO;
import com.hncboy.chatgpt.base.mapper.ChatMessageMapper;
import com.hncboy.chatgpt.base.util.PageUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-27
 * 聊天记录业务实现类
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessageDO> implements ChatMessageService {

    @Override
    public IPage<ChatMessageVO> pageChatMessage(ChatMessagePageQuery chatMessagePageQuery) {
        Page<ChatMessageDO> chatMessagePage = page(new Page<>(chatMessagePageQuery.getPageNum(), chatMessagePageQuery.getPageSize()), new LambdaQueryWrapper<ChatMessageDO>()
                // 聊天内容模糊查询
                .like(StrUtil.isNotBlank(chatMessagePageQuery.getContent()), ChatMessageDO::getContent, chatMessagePageQuery.getContent())
                // IP 模糊查询
                .like(StrUtil.isNotBlank(chatMessagePageQuery.getIp()), ChatMessageDO::getIp, chatMessagePageQuery.getIp())
                // 查询指定聊天室
                .eq(Objects.nonNull(chatMessagePageQuery.getChatRoomId()), ChatMessageDO::getChatRoomId, chatMessagePageQuery.getChatRoomId())
                .orderByDesc(ChatMessageDO::getCreateTime));

        return PageUtil.toPage(chatMessagePage, ChatMessageConverter.INSTANCE.entityToVO(chatMessagePage.getRecords()));
    }
}
