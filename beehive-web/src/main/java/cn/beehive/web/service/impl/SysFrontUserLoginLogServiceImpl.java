package cn.beehive.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.beehive.base.domain.entity.SysFrontUserLoginLogDO;
import cn.beehive.base.enums.FrontUserRegisterTypeEnum;
import cn.beehive.base.mapper.SysFrontUserLoginLogMapper;
import cn.beehive.base.util.WebUtil;
import cn.beehive.web.service.FrontUserBaseService;
import cn.beehive.web.service.SysFrontUserLoginLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 前端用户登录日志业务实现类
 *
 * @author CoDeleven
 */
@Service
public class SysFrontUserLoginLogServiceImpl extends ServiceImpl<SysFrontUserLoginLogMapper, SysFrontUserLoginLogDO> implements SysFrontUserLoginLogService {

    @Resource
    private FrontUserBaseService frontUserBaseService;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
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

        // 更新登录信息
        frontUserBaseService.updateLastLoginIp(baseUserId);
    }
}




