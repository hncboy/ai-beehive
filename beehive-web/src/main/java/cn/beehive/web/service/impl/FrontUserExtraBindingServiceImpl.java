package cn.beehive.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.beehive.base.domain.entity.FrontUserBaseDO;
import cn.beehive.base.domain.entity.FrontUserExtraBindingDO;
import cn.beehive.base.domain.entity.FrontUserExtraEmailDO;
import cn.beehive.base.enums.FrontUserRegisterTypeEnum;
import cn.beehive.base.enums.UserExtraBindingTypeEnum;
import cn.beehive.base.mapper.FrontUserExtraBindingMapper;
import cn.beehive.web.service.FrontUserExtraBindingService;
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




