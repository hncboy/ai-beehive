package com.hncboy.chatgpt.base.service;

import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraBindingDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;

/**
 * 针对表【front_user_extra_binding(前端用户绑定表)】的数据库操作Service
* @author CoDeleven
*/
public interface FrontUserExtraBindingService extends IService<FrontUserExtraBindingDO> {

    void bindEmail(FrontUserBaseDO baseUser, FrontUserExtraEmailDO emailInfo);

    FrontUserExtraBindingDO findBindingRelations(FrontUserRegisterTypeEnum frontUserRegisterTypeEnum, Integer extraInfoId);
}
