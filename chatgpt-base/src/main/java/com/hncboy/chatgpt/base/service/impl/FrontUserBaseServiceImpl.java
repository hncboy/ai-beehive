package com.hncboy.chatgpt.base.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;
import com.hncboy.chatgpt.base.mapper.FrontUserBaseMapper;
import com.hncboy.chatgpt.base.service.FrontUserBaseService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 基础用户服务实现类
 * @author CoDeleven
 */
@Service
public class FrontUserBaseServiceImpl extends ServiceImpl<FrontUserBaseMapper, FrontUserBaseDO> implements FrontUserBaseService {

    @Override
    public FrontUserBaseDO createEmptyBaseUser() {
        FrontUserBaseDO userBaseDO = new FrontUserBaseDO();
        userBaseDO.setNickname("StarGPT_" + RandomUtil.randomString(6));
        userBaseDO.setCreateTime(new Date());
        userBaseDO.setUpdateTime(new Date());
        userBaseDO.setLastLoginIp(null);
        userBaseDO.setDescription(null);
        userBaseDO.setAvatarVersion(0);
        this.save(userBaseDO);
        return userBaseDO;
    }

    @Override
    public FrontUserBaseDO findUserInfoById(Integer baseUserId) {
        return this.getOne(new QueryWrapper<FrontUserBaseDO>().eq("id", baseUserId));
    }
}
