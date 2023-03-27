package com.hncboy.chatgpt.front.service;

import com.hncboy.chatgpt.front.domain.request.VerifySecretRequest;
import com.hncboy.chatgpt.front.domain.vo.ApiModelVO;

/**
 * @author hncboy
 * @date 2023/3/22 20:04
 * 鉴权相关业务接口
 */
public interface AuthService {

    /**
     * 验证密码
     *
     * @param verifySecretRequest 验证密码请求参数
     * @return 校验结果
     */
    String verifySecretKey(VerifySecretRequest verifySecretRequest);

    /**
     * 获取 API 模型信息
     *
     * @return 模型信息
     */
    ApiModelVO getApiModel();
}
