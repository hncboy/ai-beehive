package com.hncboy.chatgpt.base.util;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.RandomUtil;
import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * 简易图形验证码工具
 *
 * @author CoDeleven
 */
@UtilityClass
public class SimpleCaptchaUtil {

    /**
     * 建立 picCodeSessionId 和 Captcha 的关系，缓存时间180秒
     */
    private final static Cache<String, LineCaptcha> CAPTCHA_CACHE = CacheUtil.newTimedCache(180_000);

    /**
     * 获取验证码图片Base64，并存储 token 和 验证码关系
     *
     * @param picCodeSessionId 图片会话，用来比对当前验证码属于哪一次请求
     * @return 图片Base64
     */
    public String getBase64Captcha(String picCodeSessionId) {
        // 每次获取时，建立 picCodeToken <-> captcha 的关系
        LineCaptcha captcha = generateCaptchaObject();
        CAPTCHA_CACHE.put(picCodeSessionId, captcha);
        return captcha.getImageBase64();
    }

    /**
     * 验证图形验证码是否正确
     *
     * @param picCodeSessionId 图片会话，用来比对当前验证码属于哪一次请求
     * @param userInputCode    用户输入的验证码
     * @return true，用户输入的正确；false，用户输入的错误
     */
    public boolean verifyCaptcha(String picCodeSessionId, String userInputCode) {
        LineCaptcha captcha = CAPTCHA_CACHE.get(picCodeSessionId);
        if (Objects.isNull(captcha)) {
            return false;
        }
        return captcha.verify(userInputCode);
    }

    /**
     * 生成验证码对象
     * ！挺费内存的，因为每次产生code，图片数据都还存在！
     *
     * @return ShearCaptcha用于生成验证码
     */
    public static LineCaptcha generateCaptchaObject() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(250, 100, 6, 32);
        // 设置生成方式
        captcha.setGenerator(new CodeGenerator() {
            @Override
            public String generate() {
                return RandomUtil.randomString(6);
            }

            @Override
            public boolean verify(String code, String userInputCode) {
                return Objects.equals(code, userInputCode);
            }
        });
        return captcha;
    }
}