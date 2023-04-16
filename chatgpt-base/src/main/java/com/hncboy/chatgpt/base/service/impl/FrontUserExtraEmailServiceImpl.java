package com.hncboy.chatgpt.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.chatgpt.base.mapper.FrontUserExtraEmailMapper;
import com.hncboy.chatgpt.base.service.FrontUserExtraEmailService;
import com.hncboy.chatgpt.base.util.ThrowExceptionUtil;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 通用的前端用户邮箱登录方式服务
 *
 * @author CoDeleven
 */
@Service("CommonFrontUserExtraEmailServiceImpl")
public class FrontUserExtraEmailServiceImpl extends ServiceImpl<FrontUserExtraEmailMapper, FrontUserExtraEmailDO> implements FrontUserExtraEmailService {

    @Override
    public boolean isUsed(String username) {
        FrontUserExtraEmailDO userExtraEmail = this.getOne(new LambdaQueryWrapper<FrontUserExtraEmailDO>()
                .select(FrontUserExtraEmailDO::getVerified, FrontUserExtraEmailDO::getId)
                .eq(FrontUserExtraEmailDO::getUsername, username));
        if (Objects.isNull(userExtraEmail)) {
            return false;
        }
        return userExtraEmail.getVerified();
    }

    @Override
    public FrontUserExtraEmailDO getUnverifiedEmailAccount(String identity) {
        return this.getOne(new LambdaQueryWrapper<FrontUserExtraEmailDO>()
                .eq(FrontUserExtraEmailDO::getUsername, identity).eq(FrontUserExtraEmailDO::getVerified, false));
    }

    @Override
    public FrontUserExtraEmailDO getEmailAccount(String username) {
        return this.getOne(new LambdaQueryWrapper<FrontUserExtraEmailDO>()
                .eq(FrontUserExtraEmailDO::getUsername, username));
    }

    @Override
    public void verifySuccess(FrontUserExtraEmailDO emailExtraInfo) {
        ThrowExceptionUtil.isFalse(update(new FrontUserExtraEmailDO(), new LambdaUpdateWrapper<FrontUserExtraEmailDO>()
                        .set(FrontUserExtraEmailDO::getVerified, true)
                        .eq(FrontUserExtraEmailDO::getVerified, false)
                        .eq(FrontUserExtraEmailDO::getId, emailExtraInfo.getId())))
                .throwMessage("邮箱验证码失败");
    }
}