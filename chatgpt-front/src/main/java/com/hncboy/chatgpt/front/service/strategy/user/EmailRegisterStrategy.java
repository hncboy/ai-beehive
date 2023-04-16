package com.hncboy.chatgpt.front.service.strategy.user;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.constant.ApplicationConstant;
import com.hncboy.chatgpt.base.domain.entity.EmailVerifyCodeDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraBindingDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.exception.ServiceException;
import com.hncboy.chatgpt.base.service.*;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import com.hncboy.chatgpt.front.domain.request.RegisterFrontUserForEmailRequest;
import com.hncboy.chatgpt.front.domain.vo.LoginInfoVO;
import com.hncboy.chatgpt.front.domain.vo.UserInfoVO;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

import static com.hncboy.chatgpt.base.constant.ApplicationConstant.FRONT_JWT_BASE_USER_ID;
import static com.hncboy.chatgpt.base.constant.ApplicationConstant.FRONT_JWT_USERNAME;

/**
 * 邮箱注册策略
 *
 * @author CoDeleven
 */
@Lazy
@Component("EmailRegisterStrategy")
public class EmailRegisterStrategy extends RegisterTypeStrategy{
    @Resource
    private FrontUserExtraEmailService userExtraEmailService;
    @Resource
    private FrontUserBaseService baseUserService;
    @Resource
    private EmailVerifyCodeService emailVerifyCodeService;
    @Resource
    private FrontUserExtraBindingService bindingService;
    @Resource
    private EmailService emailService;
    @Resource
    private SysFrontUserLoginLogService loginLogService;

    @Override
    public boolean identityUsed(String identity) {
        return userExtraEmailService.isUsed(identity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean checkVerifyCode(String identity, String verifyCode) {
        // 校验邮箱验证码
        EmailVerifyCodeDO availableVerifyCode = emailVerifyCodeService.findAvailableByVerifyCode(verifyCode);
        if(Objects.isNull(availableVerifyCode)) {
            throw new ServiceException("验证码不存在或已过期，请重新发起...");
        }
        // 验证通过，生成基础用户信息并做绑定
        FrontUserBaseDO baseUser = baseUserService.createEmptyBaseUser();
        // 获取邮箱信息表
        FrontUserExtraEmailDO emailExtraInfo = userExtraEmailService.getUnverifiedEmailAccount(availableVerifyCode.getToEmailAddress());
        // 绑定两张表
        bindingService.bindEmail(baseUser, emailExtraInfo);
        // 验证完毕，写入日志
        emailVerifyCodeService.verifySuccess(availableVerifyCode);
        // 设置邮箱已验证
        userExtraEmailService.verifySuccess(emailExtraInfo);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean register(RegisterFrontUserForEmailRequest request) {
        // 注册前会校验账号信息，到注册时能确保账号都是可以注册的

        // 查找邮箱账号是否存在
        FrontUserExtraEmailDO existsEmailDO = userExtraEmailService.getUnverifiedEmailAccount(request.getIdentity());
        String salt = RandomUtil.randomString(6);
        // 构建新的邮箱信息
        if(Objects.isNull(existsEmailDO)) {
            existsEmailDO = FrontUserExtraEmailDO.builder()
                    .password(this.encryptRawPassword(request.getPassword(), salt))
                    .salt(salt)
                    .username(request.getIdentity())
                    .verified(false)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            // 存储邮箱信息
            userExtraEmailService.save(existsEmailDO);
        } else {
            // 在未使用的邮箱基础上更新下密码信息，然后重新投入使用
            existsEmailDO.setSalt(salt);
            existsEmailDO.setVerified(false);
            existsEmailDO.setPassword(this.encryptRawPassword(request.getPassword(), salt));
            existsEmailDO.setUpdateTime(new Date());
            // 存储邮箱信息
            userExtraEmailService.updateById(existsEmailDO);
        }
        // 存储验证码记录
        EmailVerifyCodeDO emailVerifyCodeDO = emailVerifyCodeService.createVerifyCode(EmailBizTypeEnum.REGISTER_VERIFY, request.getIdentity());

        // 发送邮箱验证信息
        emailService.sendForVerifyCode(request.getIdentity(), emailVerifyCodeDO.getVerifyCode());
        return true;
    }

    @Override
    public UserInfoVO getLoginUserInfo(Integer extraInfoId) {
        // 根据注册类型+extraInfoId获取 当前邮箱绑定在了哪个用户上
        FrontUserExtraBindingDO bindingRelations = bindingService.findBindingRelations(FrontUserRegisterTypeEnum.EMAIL, extraInfoId);
        if(Objects.isNull(bindingRelations)) {
            throw new ServiceException(StrUtil.format("注册方式：{} 额外信息ID：{} 绑定关系不存在",
                    FrontUserRegisterTypeEnum.EMAIL.getDesc(), extraInfoId));
        }
        // 根据绑定关系查找基础用户信息
        FrontUserBaseDO baseUser = baseUserService.findUserInfoById(bindingRelations.getBaseUserId());
        if(Objects.isNull(baseUser)) {
            throw new ServiceException(StrUtil.format("基础用户不存在：{}", bindingRelations.getBaseUserId()));
        }
        // 封装基础用户信息并返回
        return UserInfoVO.builder().baseUserId(baseUser.getId())
                .description(baseUser.getDescription())
                .nickname(baseUser.getNickname())
                .avatarUrl("").build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginInfoVO login(String username, String password) {
        // 验证账号信息
        FrontUserExtraEmailDO account = userExtraEmailService.getEmailAccount(username);
        if(Objects.isNull(account)) {
            throw new ServiceException("账号或密码错误");
        }
        // 二次加密，验证账号密码
        String salt = account.getSalt();
        String afterEncryptedPassword = this.encryptRawPassword(password, salt);
        if(!Objects.equals(afterEncryptedPassword, account.getPassword())) {
            loginLogService.loginFailed(FrontUserRegisterTypeEnum.EMAIL, account.getId(), 0, "账号或密码错误");
            throw new ServiceException("账号或密码错误");
        }
        // 获取登录用户信息
        UserInfoVO userInfo = this.getLoginUserInfo(account.getId());

        // 执行登录
        StpUtil.login(account.getId(), SaLoginModel.create()
                .setExtra(FRONT_JWT_USERNAME, account.getUsername())
                .setExtra(ApplicationConstant.FRONT_JWT_REGISTER_TYPE_CODE, FrontUserRegisterTypeEnum.EMAIL.getCode())
                .setExtra(FRONT_JWT_BASE_USER_ID, userInfo.getBaseUserId()));

        // 记录登录日志
        loginLogService.loginSuccess(FrontUserRegisterTypeEnum.EMAIL, account.getId(), userInfo.getBaseUserId());
        return LoginInfoVO.builder().token(StpUtil.getTokenValue()).baseUserId(userInfo.getBaseUserId()).build();
    }
}
