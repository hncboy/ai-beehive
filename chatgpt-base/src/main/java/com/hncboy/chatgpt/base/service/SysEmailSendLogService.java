package com.hncboy.chatgpt.base.service;

import com.hncboy.chatgpt.base.domain.entity.SysEmailSendLogDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;

/**
 * 针对表【sys_email_send_log(邮箱发送日志)】的数据库操作Service
* @author CoDeleven
*/
public interface SysEmailSendLogService extends IService<SysEmailSendLogDO> {
    /**
     * 创建邮件发送成功的日志
     *
     * @param messageId 邮件msgId
     * @param from 发件人地址
     * @param to 收件人地址
     * @param bizType 业务类型
     * @param content 发送内容
     */
    void createSuccessLogBySysLog(String messageId, String from, String to, EmailBizTypeEnum bizType, String content);

    /**
     * 创建邮件发送失败的日志
     *
     * @param messageId 邮件msgId
     * @param from 发件人地址
     * @param to 收件人地址
     * @param bizType 业务类型
     * @param content 发送内容
     */
    void createFailedLogBySysLog(String messageId, String from, String to, EmailBizTypeEnum bizType, String content, String failedMsg);
}
