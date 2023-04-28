package com.hncboy.chatgpt.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraBindingDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.enums.UserExtraBindingTypeEnum;
import com.hncboy.chatgpt.base.mapper.FrontUserExtraBindingMapper;
import com.hncboy.chatgpt.front.service.FrontUserExtraBindingService;
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




