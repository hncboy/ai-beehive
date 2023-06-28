package com.hncboy.beehive.web.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.beehive.base.domain.entity.FrontUserBaseDO;
import com.hncboy.beehive.base.mapper.FrontUserBaseMapper;
import com.hncboy.beehive.base.util.WebUtil;
import com.hncboy.beehive.web.service.FrontUserBaseService;
import org.springframework.stereotype.Service;

/**
 * 基础用户业务实现类
 *
 * @author CoDeleven
 */
@Service
public class FrontUserBaseServiceImpl extends ServiceImpl<FrontUserBaseMapper, FrontUserBaseDO> implements FrontUserBaseService {

    @Override
    public FrontUserBaseDO createEmptyBaseUser() {
        FrontUserBaseDO userBaseDO = new FrontUserBaseDO();
        userBaseDO.setNickname("StarGPT_" + RandomUtil.randomString(6));
        userBaseDO.setLastLoginIp(null);
        userBaseDO.setDescription(null);
        userBaseDO.setAvatarVersion(0);
        this.save(userBaseDO);
        return userBaseDO;
    }

    @Override
    public FrontUserBaseDO findUserInfoById(Integer baseUserId) {
        return getById(baseUserId);
    }

    @Override
    public void updateLastLoginIp(Integer baseUserId) {
        update(new FrontUserBaseDO(), new LambdaUpdateWrapper<FrontUserBaseDO>()
                .set(FrontUserBaseDO::getLastLoginIp, WebUtil.getIp())
                .eq(FrontUserBaseDO::getId, baseUserId));
    }
}
