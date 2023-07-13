package com.hncboy.beehive.web.service.strategy.user;

import cn.hutool.core.lang.Pair;
import com.hncboy.beehive.base.constant.ApplicationConstant;
import com.hncboy.beehive.base.domain.entity.EmailVerifyCodeDO;
import com.hncboy.beehive.base.domain.entity.FrontUserBaseDO;
import com.hncboy.beehive.base.domain.entity.FrontUserExtraBindingDO;
import com.hncboy.beehive.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.beehive.base.enums.EmailBizTypeEnum;
import com.hncboy.beehive.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.beehive.base.enums.FrontUserStatusEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.resource.email.EmailRegisterLoginConfig;
import com.hncboy.beehive.base.resource.email.EmailUtil;
import com.hncboy.beehive.web.domain.request.RegisterFrontUserForEmailRequest;
import com.hncboy.beehive.web.domain.vo.LoginInfoVO;
import com.hncboy.beehive.web.domain.vo.UserInfoVO;
import com.hncboy.beehive.web.service.EmailService;
import com.hncboy.beehive.web.service.EmailVerifyCodeService;
import com.hncboy.beehive.web.service.FrontUserBaseService;
import com.hncboy.beehive.web.service.FrontUserExtraBindingService;
import com.hncboy.beehive.web.service.FrontUserExtraEmailService;
import com.hncboy.beehive.web.service.SysFrontUserLoginLogService;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.hncboy.beehive.base.constant.ApplicationConstant.FRONT_JWT_EXTRA_USER_ID;
import static com.hncboy.beehive.base.constant.ApplicationConstant.FRONT_JWT_USERNAME;

/**
 * 邮箱注册策略
 *
 * @author CoDeleven
 */
@Lazy
@Component("EmailRegisterStrategy")
public class EmailAbstractRegisterStrategy extends AbstractRegisterTypeStrategy {

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
    public void checkVerifyCode(String identity, String verifyCode) {
        // 校验邮箱验证码
        EmailVerifyCodeDO availableVerifyCode = emailVerifyCodeService.findAvailableByVerifyCode(verifyCode);
        if (Objects.isNull(availableVerifyCode)) {
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
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Pair<Boolean, String> register(RegisterFrontUserForEmailRequest request) {
        // 校验邮箱注册权限
        EmailRegisterLoginConfig emailRegisterAccountConfig = EmailUtil.getRegisterAccountConfig();
        emailRegisterAccountConfig.checkRegisterPermission(request.getIdentity());

        // 查找邮箱账号是否存在
        FrontUserExtraEmailDO existsEmailDO = userExtraEmailService.getUnverifiedEmailAccount(request.getIdentity());
        String salt = RandomUtil.randomString(6);
        // 构建新的邮箱信息
        if (Objects.isNull(existsEmailDO)) {
            existsEmailDO = FrontUserExtraEmailDO.builder()
                    .password(this.encryptRawPassword(request.getPassword(), salt))
                    .salt(salt)
                    .username(request.getIdentity())
                    .verified(false)
                    .build();
            // 存储邮箱信息
            userExtraEmailService.save(existsEmailDO);
        } else {
            // 在未使用的邮箱基础上更新下密码信息，然后重新投入使用
            existsEmailDO.setSalt(salt);
            existsEmailDO.setVerified(false);
            existsEmailDO.setPassword(this.encryptRawPassword(request.getPassword(), salt));
            // 存储邮箱信息
            userExtraEmailService.updateById(existsEmailDO);
        }
        // 存储验证码记录
        EmailVerifyCodeDO emailVerifyCodeDO = emailVerifyCodeService.createVerifyCode(EmailBizTypeEnum.REGISTER_VERIFY, request.getIdentity());

        // 发送邮箱验证信息
        return emailService.sendForVerifyCode(request.getIdentity(), emailVerifyCodeDO.getVerifyCode());
    }

    @Override
    public UserInfoVO getLoginUserInfo(Integer extraInfoId) {
        FrontUserExtraEmailDO extraEmailDO = userExtraEmailService.getById(extraInfoId);

        // 根据注册类型 + extraInfoId 获取 当前邮箱绑定在了哪个用户上
        FrontUserExtraBindingDO bindingRelations = bindingService.findExtraBinding(FrontUserRegisterTypeEnum.EMAIL, extraInfoId);
        if (Objects.isNull(bindingRelations)) {
            throw new ServiceException(StrUtil.format("注册方式：{} 额外信息ID：{} 绑定关系不存在",
                    FrontUserRegisterTypeEnum.EMAIL.getDesc(), extraInfoId));
        }
        // 根据绑定关系查找基础用户信息
        FrontUserBaseDO frontUserBaseDO = baseUserService.findUserInfoById(bindingRelations.getBaseUserId());
        if (Objects.isNull(frontUserBaseDO)) {
            throw new ServiceException(StrUtil.format("基础用户不存在：{}", bindingRelations.getBaseUserId()));
        }

        // 封装基础用户信息并返回
        return UserInfoVO.builder().baseUserId(frontUserBaseDO.getId())
                .description(frontUserBaseDO.getDescription())
                .nickname(frontUserBaseDO.getNickname())
                .email(extraEmailDO.getUsername())
                .status(frontUserBaseDO.getStatus())
                .avatarUrl("").build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginInfoVO login(String username, String password) {
        // 校验邮箱登录权限
        EmailRegisterLoginConfig emailRegisterAccountConfig = EmailUtil.getRegisterAccountConfig();
        emailRegisterAccountConfig.checkLoginPermission(username);

        // 验证账号信息
        FrontUserExtraEmailDO emailDO = userExtraEmailService.getEmailAccount(username);
        if (Objects.isNull(emailDO) || BooleanUtil.isFalse(emailDO.getVerified())) {
            throw new ServiceException("邮箱未注册");
        }

        // 二次加密，验证账号密码
        String afterEncryptedPassword = encryptRawPassword(password, emailDO.getSalt());

        // 密码不一致的情况
        if (ObjectUtil.notEqual(afterEncryptedPassword, emailDO.getPassword())) {
            Integer baseUserId = 0;
            // 获取绑定的基础用户 id
            FrontUserExtraBindingDO userExtraBindingDO = bindingService.findExtraBinding(FrontUserRegisterTypeEnum.EMAIL, emailDO.getId());
            if (Objects.nonNull(userExtraBindingDO)) {
                FrontUserBaseDO frontUserBaseDO = baseUserService.findUserInfoById(userExtraBindingDO.getBaseUserId());
                if (Objects.nonNull(frontUserBaseDO)) {
                    baseUserId = frontUserBaseDO.getId();
                }
            }

            // 记录登录失败日志
            loginLogService.loginFailed(FrontUserRegisterTypeEnum.EMAIL, emailDO.getId(), baseUserId, "账号或密码错误");
            throw new ServiceException("账号或密码错误");
        }

        // 获取登录用户信息
        UserInfoVO userInfoVO = getLoginUserInfo(emailDO.getId());

        // 判断用户状态
        if (userInfoVO.getStatus() == FrontUserStatusEnum.BLOCK) {
            // 记录登录失败日志
            loginLogService.loginFailed(FrontUserRegisterTypeEnum.EMAIL, emailDO.getId(), userInfoVO.getBaseUserId(), "用户被禁止登录");
            throw new ServiceException("您已经被禁止登录，有问题请联系管理员");
        } else if (userInfoVO.getStatus() == FrontUserStatusEnum.WAIT_CHECK) {
            // 记录登录失败日志
            loginLogService.loginFailed(FrontUserRegisterTypeEnum.EMAIL, emailDO.getId(), userInfoVO.getBaseUserId(), "用户等待审核");
            throw new ServiceException("您的账号等待管理员审核");
        }

        // 执行登录
        StpUtil.login(userInfoVO.getBaseUserId(), SaLoginModel.create()
                .setExtra(FRONT_JWT_USERNAME, emailDO.getUsername())
                .setExtra(ApplicationConstant.FRONT_JWT_REGISTER_TYPE_CODE, FrontUserRegisterTypeEnum.EMAIL.getCode())
                .setExtra(FRONT_JWT_EXTRA_USER_ID, emailDO.getId()));

        // 记录登录日志
        loginLogService.loginSuccess(FrontUserRegisterTypeEnum.EMAIL, emailDO.getId(), userInfoVO.getBaseUserId());

        return LoginInfoVO.builder().token(StpUtil.getTokenValue()).baseUserId(userInfoVO.getBaseUserId()).build();
    }
}