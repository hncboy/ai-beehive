package com.hncboy.beehive.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.beehive.base.domain.entity.FrontUserBaseDO;
import com.hncboy.beehive.base.domain.entity.FrontUserExtraBindingDO;
import com.hncboy.beehive.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.beehive.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.beehive.base.enums.UserExtraBindingTypeEnum;
import com.hncboy.beehive.base.mapper.FrontUserExtraBindingMapper;
import com.hncboy.beehive.web.service.FrontUserExtraBindingService;
import org.springframework.stereotype.Service;

/**
 * 前端用户绑定相关业务实现类
 *
 * @author CoDeleven
 */
@Service
public class FrontUserExtraBindingServiceImpl extends ServiceImpl<FrontUserExtraBindingMapper, FrontUserExtraBindingDO>
        implements FrontUserExtraBindingService {

    @Override
    public void bindEmail(FrontUserBaseDO baseUser, FrontUserExtraEmailDO extraEmailDO) {
        FrontUserExtraBindingDO bindingDO = new FrontUserExtraBindingDO();
        bindingDO.setBindingType(UserExtraBindingTypeEnum.BIND_EMAIL);
        bindingDO.setExtraInfoId(extraEmailDO.getId());
        bindingDO.setBaseUserId(baseUser.getId());
        this.save(bindingDO);
    }

    @Override
    public FrontUserExtraBindingDO findExtraBinding(FrontUserRegisterTypeEnum registerType, Integer extraInfoId) {
        return getOne(new LambdaQueryWrapper<FrontUserExtraBindingDO>()
                .eq(FrontUserExtraBindingDO::getBindingType, registerType)
                .eq(FrontUserExtraBindingDO::getExtraInfoId, extraInfoId));
    }
}




