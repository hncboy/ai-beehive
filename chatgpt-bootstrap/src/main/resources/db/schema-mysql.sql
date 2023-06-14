-- 创建 database
CREATE DATABASE IF NOT EXISTS chat DEFAULT CHARACTER SET utf8;

-- 进入 chat 库
USE chat;

CREATE TABLE IF NOT EXISTS `chat_message`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 id',
    `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息 id',
    `parent_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级消息 id',
    `parent_answer_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级回答消息 id',
    `parent_question_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级问题消息 id',
    `context_count` bigint NOT NULL COMMENT '上下文数量',
    `question_context_count` bigint NOT NULL COMMENT '问题上下文数量',
    `message_type` int NOT NULL COMMENT '消息类型枚举',
    `chat_room_id` bigint NOT NULL COMMENT '聊天室 id',
    `conversation_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对话 id',
    `api_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API 类型',
    `model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模型名称',
    `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ApiKey',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
    `original_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息的原始请求或响应数据',
    `response_error_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误的响应数据',
    `prompt_tokens` bigint NULL DEFAULT NULL COMMENT '输入消息的 tokens',
    `completion_tokens` bigint NULL DEFAULT NULL COMMENT '输出消息的 tokens',
    `total_tokens` bigint NULL DEFAULT NULL COMMENT '累计 Tokens',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip',
    `status` int NOT NULL COMMENT '聊天记录状态',
    `is_hide` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否隐藏 0 否 1 是',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    `update_time` timestamp NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `message_id`(`message_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_room
-- ----------------------------
CREATE TABLE IF NOT EXISTS `chat_room`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 id',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ip',
    `conversation_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对话 id，唯一',
    `first_chat_message_id` bigint NOT NULL COMMENT '第一条消息主键',
    `first_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '第一条消息',
    `title` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对话标题，从第一条消息截取',
    `api_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API 类型',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    `update_time` timestamp NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `first_chat_message_id`(`first_chat_message_id` ASC) USING BTREE,
    UNIQUE INDEX `first_message_id`(`first_message_id` ASC) USING BTREE,
    UNIQUE INDEX `conversation_id`(`conversation_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天室表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for email_verify_code
-- ----------------------------
CREATE TABLE IF NOT EXISTS `email_verify_code`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `to_email_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码接收邮箱地址',
    `verify_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
    `is_used` tinyint(1) NOT NULL COMMENT '是否使用 0 否 1 是',
    `verify_ip` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '核销IP，方便识别一些机器人账号',
    `expire_at` datetime NOT NULL COMMENT '验证码过期时间',
    `biz_type` tinyint NOT NULL COMMENT '当前邮箱业务',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_verify_code`(`verify_code` ASC) USING BTREE COMMENT '防止验证码重复'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮箱验证码核销记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for front_user_base
-- ----------------------------
CREATE TABLE IF NOT EXISTS `front_user_base`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `nickname` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
    `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
    `avatar_version` int NOT NULL COMMENT '头像版本号',
    `last_login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上一次登录 IP',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '前端用户基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for front_user_extra_binding
-- ----------------------------
CREATE TABLE IF NOT EXISTS `front_user_extra_binding`  (
     `id` int NOT NULL AUTO_INCREMENT,
     `binding_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '绑定类型,qq,wechat,sina,github,email,phone',
     `extra_info_id` int NOT NULL COMMENT '额外信息表ID',
     `base_user_id` int NOT NULL COMMENT '基础用户表的ID',
     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE INDEX `front_user_extra_binding_pk`(`binding_type` ASC, `base_user_id` ASC, `extra_info_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '前端用户绑定表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for front_user_extra_email
-- ----------------------------
CREATE TABLE IF NOT EXISTS `front_user_extra_email`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱账号',
    `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密后的密码',
    `salt` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密盐',
    `verified` tinyint NOT NULL DEFAULT 0 COMMENT '是否验证过，0 否 1 是',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `front_user_extra_email_pk`(`username` ASC) USING BTREE COMMENT '邮箱唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '前端用户邮箱登录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sensitive_word
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sensitive_word`  (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '敏感词内容',
    `status` int NOT NULL COMMENT '状态 1 启用 2 停用',
    `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除 0 否 NULL 是',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '敏感词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_email_send_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_email_send_log`  (
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
   `from_email_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发件人邮箱',
   `to_email_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人邮箱',
   `biz_type` int NOT NULL COMMENT '业务类型',
   `request_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求 ip',
   `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送内容',
   `message_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送后会返回一个messageId',
   `status` tinyint NOT NULL COMMENT '发送状态，0失败，1成功',
   `message` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送后的消息，用于记录成功/失败的信息，成功默认为success',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮箱发送日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_front_user_login_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_front_user_login_log`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `base_user_id` int NOT NULL COMMENT '登录的基础用户ID',
    `login_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录方式（注册方式），邮箱登录，手机登录等等',
    `login_extra_info_id` int NOT NULL COMMENT '登录信息ID与login_type有关联，邮箱登录时关联front_user_extra_email',
    `login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录的IP地址',
    `login_status` tinyint(1) NOT NULL COMMENT '登录状态，1登录成功，0登录失败',
    `message` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '结果，如果成功一律success；否则保存错误信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '前端用户登录日志表' ROW_FORMAT = Dynamic;
