package com.hncboy.chatgpt.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraBindingDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.enums.UserExtraBindingTypeEnum;
import com.hncboy.chatgpt.base.service.FrontUserExtraBindingService;
import com.hncboy.chatgpt.base.mapper.FrontUserExtraBindingMapper;
import org.springframework.stereotype.Service;

/**
 * 针对表【front_user_extra_binding(前端用户绑定表)】的数据库操作Service实现
* @author CoDeleven
*/
@Service
public class FrontUserExtraBindingServiceImpl extends ServiceImpl<FrontUserExtraBindingMapper, FrontUserExtraBindingDO>
    implements FrontUserExtraBindingService{

    @Override
    public void bindEmail(FrontUserBaseDO baseUser, FrontUserExtraEmailDO emailInfo) {
        FrontUserExtraBindingDO bindingDO = new FrontUserExtraBindingDO();
        bindingDO.setBindingType(UserExtraBindingTypeEnum.BIND_EMAIL);
        bindingDO.setExtraInfoId(emailInfo.getId());
        bindingDO.setBaseUserId(baseUser.getId());
        this.save(bindingDO);
    }

    @Override
    public FrontUserExtraBindingDO findBindingRelations(FrontUserRegisterTypeEnum registerType,
                                                        Integer extraInfoId) {
        return this.getOne(new QueryWrapper<FrontUserExtraBindingDO>()
                .eq("binding_type", registerType.getCode())
                .eq("extra_info_id", extraInfoId));
    }
}




