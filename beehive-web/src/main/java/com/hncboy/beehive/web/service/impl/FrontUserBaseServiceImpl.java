package com.hncboy.beehive.web.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.beehive.base.domain.entity.FrontUserBaseDO;
import com.hncboy.beehive.base.enums.FrontUserStatusEnum;
import com.hncboy.beehive.base.mapper.FrontUserBaseMapper;
import com.hncboy.beehive.base.resource.email.EmailRegisterLoginConfig;
import com.hncboy.beehive.base.resource.email.EmailUtil;
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
        userBaseDO.setNickname("ai_beehive_" + RandomUtil.randomString(6));
        userBaseDO.setLastLoginIp(WebUtil.getIp());
        userBaseDO.setDescription(null);
        userBaseDO.setAvatarVersion(0);
        EmailRegisterLoginConfig registerAccountConfig = EmailUtil.getRegisterAccountConfig();
        if (BooleanUtil.isTrue(registerAccountConfig.getRegisterCheckEnabled())) {
            userBaseDO.setStatus(FrontUserStatusEnum.WAIT_CHECK);
        } else {
            userBaseDO.setStatus(FrontUserStatusEnum.NORMAL);
        }
        save(userBaseDO);
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
