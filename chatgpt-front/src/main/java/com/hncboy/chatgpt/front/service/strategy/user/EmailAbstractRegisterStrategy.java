package com.hncboy.chatgpt.front.service.strategy.user;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.base.constant.ApplicationConstant;
import com.hncboy.chatgpt.base.domain.entity.EmailVerifyCodeDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserBaseDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraBindingDO;
import com.hncboy.chatgpt.base.domain.entity.FrontUserExtraEmailDO;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import com.hncboy.chatgpt.base.enums.FrontUserRegisterTypeEnum;
import com.hncboy.chatgpt.base.exception.ServiceException;
import com.hncboy.chatgpt.front.service.EmailService;
import com.hncboy.chatgpt.front.service.EmailVerifyCodeService;
import com.hncboy.chatgpt.front.service.FrontUserBaseService;
import com.hncboy.chatgpt.front.service.FrontUserExtraBindingService;
import com.hncboy.chatgpt.front.service.FrontUserExtraEmailService;
import com.hncboy.chatgpt.front.service.SysFrontUserLoginLogService;
import com.hncboy.chatgpt.front.domain.request.RegisterFrontUserForEmailRequest;
import com.hncboy.chatgpt.front.domain.vo.LoginInfoVO;
import com.hncboy.chatgpt.front.domain.vo.UserInfoVO;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.hncboy.chatgpt.base.constant.ApplicationConstant.FRONT_JWT_EXTRA_USER_ID;
import static com.hncboy.chatgpt.base.constant.ApplicationConstant.FRONT_JWT_USERNAME;

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
    public void register(RegisterFrontUserForEmailRequest request) {
        // 注册前会校验账号信息，到注册时能确保账号都是可以注册的

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

        // TODO 根据 ip 进行限流

        // 发送邮箱验证信息
        emailService.sendForVerifyCode(request.getIdentity(), emailVerifyCodeDO.getVerifyCode());
    }

    @Override
    public UserInfoVO getLoginUserInfo(Integer extraInfoId) {
        FrontUserExtraEmailDO extraEmailDO = userExtraEmailService.getById(extraInfoId);

        // 根据注册类型+extraInfoId获取 当前邮箱绑定在了哪个用户上
        FrontUserExtraBindingDO bindingRelations = bindingService.findExtraBinding(FrontUserRegisterTypeEnum.EMAIL, extraInfoId);
        if (Objects.isNull(bindingRelations)) {
            throw new ServiceException(StrUtil.format("注册方式：{} 额外信息ID：{} 绑定关系不存在",
                    FrontUserRegisterTypeEnum.EMAIL.getDesc(), extraInfoId));
        }
        // 根据绑定关系查找基础用户信息
        FrontUserBaseDO baseUser = baseUserService.findUserInfoById(bindingRelations.getBaseUserId());
        if (Objects.isNull(baseUser)) {
            throw new ServiceException(StrUtil.format("基础用户不存在：{}", bindingRelations.getBaseUserId()));
        }
        // 封装基础用户信息并返回
        return UserInfoVO.builder().baseUserId(baseUser.getId())
                .description(baseUser.getDescription())
                .nickname(baseUser.getNickname())
                .email(extraEmailDO.getUsername())
                .avatarUrl("").build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginInfoVO login(String username, String password) {
        // 验证账号信息
        FrontUserExtraEmailDO emailDO = userExtraEmailService.getEmailAccount(username);
        if (Objects.isNull(emailDO) || BooleanUtil.isFalse(emailDO.getVerified())) {
            throw new ServiceException("邮箱未注册");
        }

        // 二次加密，验证账号密码
        String salt = emailDO.getSalt();
        String afterEncryptedPassword = this.encryptRawPassword(password, salt);
        if (!Objects.equals(afterEncryptedPassword, emailDO.getPassword())) {
            Integer baseUserId = 0;
            // 获取绑定的基础用户 id
            FrontUserExtraBindingDO userExtraBindingDO = bindingService.findExtraBinding(FrontUserRegisterTypeEnum.EMAIL, emailDO.getId());
            if (Objects.nonNull(userExtraBindingDO)) {
                FrontUserBaseDO userBaseDO = baseUserService.findUserInfoById(userExtraBindingDO.getBaseUserId());
                if (Objects.nonNull(userBaseDO)) {
                    baseUserId = userBaseDO.getId();
                }
            }

            // 记录登录失败日志
            loginLogService.loginFailed(FrontUserRegisterTypeEnum.EMAIL, emailDO.getId(), baseUserId, "账号或密码错误");
            throw new ServiceException("账号或密码错误");
        }

        // 获取登录用户信息
        UserInfoVO userInfo = this.getLoginUserInfo(emailDO.getId());

        // 执行登录
        StpUtil.login(userInfo.getBaseUserId(), SaLoginModel.create()
                .setExtra(FRONT_JWT_USERNAME, emailDO.getUsername())
                .setExtra(ApplicationConstant.FRONT_JWT_REGISTER_TYPE_CODE, FrontUserRegisterTypeEnum.EMAIL.getCode())
                .setExtra(FRONT_JWT_EXTRA_USER_ID, emailDO.getId()));

        // 记录登录日志
        loginLogService.loginSuccess(FrontUserRegisterTypeEnum.EMAIL, emailDO.getId(), userInfo.getBaseUserId());

        return LoginInfoVO.builder().token(StpUtil.getTokenValue()).baseUserId(userInfo.getBaseUserId()).build();
    }
}