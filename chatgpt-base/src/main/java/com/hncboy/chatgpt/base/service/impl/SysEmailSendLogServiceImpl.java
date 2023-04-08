package com.hncboy.chatgpt.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hncboy.chatgpt.base.domain.entity.SysEmailSendLogDO;
import com.hncboy.chatgpt.base.enums.EmailBizTypeEnum;
import com.hncboy.chatgpt.base.service.SysEmailSendLogService;
import com.hncboy.chatgpt.base.mapper.SysEmailSendLogMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 针对表【sys_email_send_log(邮箱发送日志)】的数据库操作Service实现
* @author CoDeleven
*/
@Service
public class SysEmailSendLogServiceImpl extends ServiceImpl<SysEmailSendLogMapper, SysEmailSendLogDO>
    implements SysEmailSendLogService{

    /**
     * 邮件发送成功的默认消息
     */
    private static final String DEFAULT_SEND_SUCCESS_MESSAGE = "success";
    private static final int STATUS_SEND_SUCCESS = 1;
    private static final int STATUS_SEND_FAILED = 0;
    @Override
    public void createSuccessLogBySysLog(String messageId, String from, String to, EmailBizTypeEnum bizType, String content) {
        SysEmailSendLogDO log = this.createLogBySysLog(messageId, from, to, bizType, content);
        log.setStatus(STATUS_SEND_SUCCESS);
        log.setMessage(DEFAULT_SEND_SUCCESS_MESSAGE);
        this.save(log);
    }

    @Override
    public void createFailedLogBySysLog(String messageId, String from, String to, EmailBizTypeEnum bizType, String content, String failedMsg) {
        SysEmailSendLogDO log = this.createLogBySysLog(messageId, from, to, bizType, content);
        log.setStatus(STATUS_SEND_FAILED);
        log.setMessage(failedMsg);
        this.save(log);
    }

    private SysEmailSendLogDO createLogBySysLog(String messageId, String from, String to, EmailBizTypeEnum bizType, String content) {
        SysEmailSendLogDO sendLog = new SysEmailSendLogDO();
        sendLog.setFromEmailAddress(from);
        sendLog.setToEmailAddress(to);
        sendLog.setContent(content);
        sendLog.setBizType(bizType);
        sendLog.setMessageId(messageId);
        // 预留字段
        sendLog.setOperatorSysUserId(1);
        sendLog.setCreateTime(new Date());
        sendLog.setUpdateTime(new Date());
        return sendLog;
    }
}




