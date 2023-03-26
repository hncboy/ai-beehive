package com.hncboy.chatgpt.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.hncboy.chatgpt.config.ChatConfig;
import com.hncboy.chatgpt.domain.request.VerifySecretRequest;
import com.hncboy.chatgpt.domain.vo.ApiModelVO;
import com.hncboy.chatgpt.exception.ServiceException;
import com.hncboy.chatgpt.service.AuthService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/3/22 20:05
 * 鉴权相关业务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private ChatConfig chatConfig;

    @Override
    public String verifySecretKey(VerifySecretRequest verifySecretRequest) {
        if (BooleanUtil.isFalse(chatConfig.hasAuth())) {
            return "未设置密码";
        }

        if (StrUtil.isEmpty(verifySecretRequest.getToken())) {
            throw new ServiceException("Secret key is empty");
        }

        if (Objects.equals(verifySecretRequest.getToken(), chatConfig.getAuthSecretKey())) {
            return "Verify successfully";
        }

        throw new ServiceException("密钥无效 | Secret key is invalid");
    }

    @Override
    public ApiModelVO getApiModel() {
        ApiModelVO apiModelVO = new ApiModelVO();
        apiModelVO.setAuth(chatConfig.hasAuth());
        apiModelVO.setModel(chatConfig.getApiTypeEnum());
        return apiModelVO;
    }
}
