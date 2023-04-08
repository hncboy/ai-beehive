package com.hncboy.chatgpt.base.mapper;

import com.hncboy.chatgpt.base.domain.entity.EmailVerifyCodeDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 针对表【email_verify_code(邮箱验证码核销记录表，记录某个邮箱发送了什么验证码，方便验证)】的数据库操作Mapper
* @author CoDeleven
*/
public interface EmailVerifyCodeMapper extends BaseMapper<EmailVerifyCodeDO> {

}




