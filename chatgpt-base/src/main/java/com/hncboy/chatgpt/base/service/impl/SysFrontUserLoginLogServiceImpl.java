package com.hncboy.chatgpt.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.SysFrontUserLoginLogDO;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.service.SysFrontUserLoginLogService;
import com.hncboy.chatgpt.base.mapper.SysFrontUserLoginLogMapper;
import com.hncboy.chatgpt.base.util.WebUtil;
import org.springframework.stereotype.Service;

/**
 * 针对表【sys_front_user_login_log(前端用户登录日志表)】的数据库操作Service实现
* @author CoDeleven
*/
@Service
public class SysFrontUserLoginLogServiceImpl extends ServiceImpl<SysFrontUserLoginLogMapper, SysFrontUserLoginLogDO>
    implements SysFrontUserLoginLogService{

    @Override
    public void loginFailed(FrontUserRegisterTypeEnum registerType, Integer extraInfoId, Integer baseUserId, String message) {
        SysFrontUserLoginLogDO logDO = new SysFrontUserLoginLogDO();
        logDO.setBaseUserId(baseUserId);
        logDO.setLoginExtraInfoId(extraInfoId);
        logDO.setLoginStatus(false);
        logDO.setLoginType(registerType);
        logDO.setMessage(message);
        logDO.setLoginIp(WebUtil.getIp());
        this.save(logDO);
    }

    @Override
    public void loginSuccess(FrontUserRegisterTypeEnum registerType, Integer extraInfoId, Integer baseUserId) {
        SysFrontUserLoginLogDO logDO = new SysFrontUserLoginLogDO();
        logDO.setBaseUserId(baseUserId);
        logDO.setLoginExtraInfoId(extraInfoId);
        logDO.setLoginStatus(true);
        logDO.setLoginType(registerType);
        logDO.setMessage("success");
        logDO.setLoginIp(WebUtil.getIp());
        this.save(logDO);
    }
}




