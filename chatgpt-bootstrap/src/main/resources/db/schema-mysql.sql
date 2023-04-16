-- 创建 database
CREATE DATABASE IF NOT EXISTS chat DEFAULT CHARACTER SET utf8;

-- 进入 chat 库
USE chat;

-- 聊天室表
CREATE TABLE IF NOT EXISTS chat_room (
    id BIGINT PRIMARY KEY COMMENT '主键',
    ip VARCHAR(255) NULL COMMENT 'ip',
    conversation_id VARCHAR(64) UNIQUE NULL COMMENT '对话 id，唯一',
    first_chat_message_id BIGINT UNIQUE NOT NULL  COMMENT '第一条消息主键',
    first_message_id VARCHAR(64) UNIQUE NOT NULL COMMENT '第一条消息',
    title VARCHAR(255) NOT NULL COMMENT '对话标题，从第一条消息截取',
    api_type VARCHAR(20) NOT NULL COMMENT 'API 类型',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT  '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天室表';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY COMMENT '主键',
    message_id VARCHAR(64) UNIQUE NOT NULL COMMENT '消息 id',
    parent_message_id VARCHAR(64) COMMENT '父级消息 id',
    parent_answer_message_id VARCHAR(64) COMMENT '父级回答消息 id',
    parent_question_message_id VARCHAR(64) COMMENT '父级问题消息 id',
    context_count BIGINT NOT NULL COMMENT '上下文数量',
    question_context_count BIGINT NOT NULL COMMENT '问题上下文数量',
    model_name VARCHAR(50) NOT NULL COMMENT '模型名称',
    message_type INTEGER NOT NULL COMMENT '消息类型枚举',
    chat_room_id BIGINT NOT NULL COMMENT '聊天室 id',
    conversation_id VARCHAR(255) NULL COMMENT '对话 id',
    api_type VARCHAR(20) NOT NULL COMMENT 'API 类型',
    api_key VARCHAR(255) NULL COMMENT 'ApiKey',
    content VARCHAR(5000) NOT NULL COMMENT '消息内容',
    original_data TEXT COMMENT '消息的原始请求或响应数据',
    response_error_data TEXT COMMENT '错误的响应数据',
    prompt_tokens INTEGER COMMENT '输入消息的 tokens',
    completion_tokens INTEGER COMMENT '输出消息的 tokens',
    total_tokens INTEGER COMMENT '累计 Tokens',
    ip VARCHAR(255) NULL COMMENT 'ip',
    status INTEGER NOT NULL COMMENT '聊天记录状态',
    is_hide TINYINT NOT NULL DEFAULT 0 COMMENT '是否隐藏 0 否 1 是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_word (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    word VARCHAR(255) NOT NULL COMMENT '敏感词内容',
    status INTEGER NOT NULL COMMENT '状态 1 启用 2 停用',
    is_deleted INTEGER DEFAULT 0 COMMENT '是否删除 0 否 NULL 是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- 发送邮箱验证码记录表
create table email_verify_code
(
    id               int auto_increment
        primary key,
    to_email_address varchar(64)                        not null comment '验证码接收邮箱地址',
    verify_code      varchar(32)                        not null comment '验证码',
    status           tinyint(1)                         not null comment '使用状态，0表示未使用，1表示已使用',
    verify_ip        char(32)                           not null comment '核销IP，方便识别一些机器人账号',
    expire_at        datetime                           not null comment '该条验证码何时过期，一般是发送时间+15分钟',
    biz_type         tinyint                            not null comment '当前邮箱业务',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) ENGINE=InnoDB comment '邮箱验证码核销记录表，记录某个邮箱发送了什么验证码，方便验证' CHARSET=utf8mb4;

-- 前端基础用户表（用户登录相关），目前简单存储了用户的一些个人信息
create table front_user_base
(
    id             int auto_increment
        primary key,
    nickname       varchar(16)                        not null comment '用户昵称',
    description    varchar(64)                        null comment '描述',
    avatar_version int                                not null comment '头像版本号',
    last_login_ip  int                                null comment '上一次登录IP地址（整数）',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) ENGINE=InnoDB comment '前端用户基础信息表，记录了用户的个人信息'  CHARSET=utf8mb4;

-- 前端用户绑定表（用户登录相关），该表记录 基础用户 和 登录方式 的绑定关系
create table front_user_extra_binding
(
    id            int auto_increment
        primary key,
    binding_type  varchar(16)                        not null comment '绑定类型,qq,wechat,sina,github,email,phone',
    extra_info_id int                                not null comment '额外信息表ID',
    base_user_id  int                                not null comment '基础用户表的ID',
    create_time   datetime default CURRENT_TIMESTAMP not null,
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint front_user_extra_binding_pk2
        unique (binding_type, base_user_id, extra_info_id)
) ENGINE=InnoDB comment '前端用户绑定表，记录了 基础用户 和 登录方式 的绑定关系' CHARSET=utf8mb4;

-- 前端用户邮箱登录信息表（用户登录相关），该表记录了使用邮箱注册的用户信息
create table front_user_extra_email
(
    id          int auto_increment
        primary key,
    username    varchar(64)                        not null comment '邮箱账号',
    password    varchar(128)                       not null comment '加密后的密码',
    salt        varchar(16)                        not null comment '加密盐',
    verified    tinyint  default 0                 not null comment '是否验证过',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint front_user_extra_email_pk2
        unique (username)
) ENGINE=InnoDB comment '前端用户邮箱登录，记录了使用邮箱注册的用户信息' CHARSET=utf8mb4;

-- 系统表，记录邮件发送日志，目前仅审计用途
create table sys_email_send_log
(
    id                   bigint auto_increment
        primary key,
    from_email_address   varchar(64)                        not null comment '发件人',
    to_email_address     varchar(64)                        not null comment '收件人',
    biz_type             int                                not null comment '业务类型',
    content              text                               not null comment '发送内容',
    message_id           varchar(128)                       not null comment '发送后会返回一个messageId',
    status               tinyint                            not null comment '发送状态，0失败，1成功',
    message              varchar(128)                       not null comment '发送后的消息，用于记录成功/失败的信息，成功默认为success',
    create_time          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time          datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    operator_sys_user_id int      default 1                 not null comment '操作人用户ID，预留字段，默认为1'
)ENGINE=InnoDB comment '邮箱发送日志，用于审计用途' CHARSET=utf8mb4;

-- 系统表，前端用户登录日志
create table sys_front_user_login_log
(
    id                  int auto_increment
        primary key,
    base_user_id        int                                not null comment '登录的基础用户ID',
    login_type          varchar(32)                        not null comment '登录方式（注册方式），邮箱登录，手机登录等等',
    login_extra_info_id int                                not null comment '登录信息ID与login_type有关联，邮箱登录时关联front_user_extra_email',
    login_ip            varchar(32)                        not null comment '登录的IP地址',
    login_status        tinyint(1)                         not null comment '登录状态，1登录成功，0登录失败',
    message             varchar(64)                        not null comment '结果，如果成功一律success；否则保存错误信息',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '发生时间'
)ENGINE=InnoDB comment '前端用户登录日志表' CHARSET=utf8mb4;
