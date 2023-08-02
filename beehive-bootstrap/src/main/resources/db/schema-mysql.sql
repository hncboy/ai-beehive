-- 创建 database
CREATE DATABASE IF NOT EXISTS ai_beehive DEFAULT CHARACTER SET utf8mb4;

-- 进入 chat 库
USE ai_beehive;

CREATE TABLE IF NOT EXISTS `bh_cell`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '封面',
    `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一编码',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序，值大的排前面',
    `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
    `introduce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '介绍',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_code`(`code` ASC) USING BTREE COMMENT '唯一编码'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'cell 表实体类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_cell
-- ----------------------------
INSERT INTO `bh_cell` VALUES (1, 'NewBing', 'https://img1.imgtp.com/2023/07/25/uFQSFjPu.ico', 'new_bing', 88, 'closed', '新必应', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (2, 'Midjourney', 'https://img1.imgtp.com/2023/07/04/PWdsPUAw.jpg', 'Midjourney', 87, 'closed', 'Midjourney', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (3, 'OpenAi Chat GPT3.5', 'https://img1.imgtp.com/2023/07/25/lNqXPOvs.ico', 'openai_chat_api_3_5', 99, 'closed', 'OpenAi Chat GPT 3.5', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (4, 'Azure', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'Azure', 0, 'wait_coding', '微软 Azure', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (5, 'Claude', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'Claude', 0, 'wait_coding', 'Claude', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (6, 'Bard', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'Bard', 0, 'wait_coding', '谷歌 Bard', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (7, 'Alpaca', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'Alpaca', 0, 'wait_coding', 'Alpaca', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (8, 'Vicuna', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'Vicuna', 0, 'wait_coding', 'Vicuna', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (9, 'ChatGLM', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'ChatGLM', 0, 'wait_coding', 'ChatGLM', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (10, 'Gradio', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'Gradio', 0, 'wait_coding', 'Gradio', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (12, 'stable_diffusion', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'stable_diffusion', 0, 'wait_coding', 'stablediffusion', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (15, 'OpenAi Embeddings', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'openai_embeddings', 0, 'wait_coding', 'OpenAi Embeddings', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (16, '官网 ChatGPT 3.5', 'https://img1.imgtp.com/2023/07/04/MtZUWCwk.png', 'openai_chat_web_3_5', 89, 'closed', '官网 ChatGPT 3.5', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (17, '官网 ChatGPT 4', 'https://img1.imgtp.com/2023/07/04/MtZUWCwk.png', 'openai_chat_web_4', 90, 'closed', '官网 ChatGPT 4', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (18, 'OpenAi Image', 'https://img1.imgtp.com/2023/07/25/lNqXPOvs.ico', 'openai_image', 97, 'closed', 'OpenAi Image', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (19, 'OpenAi Chat GPT4', 'https://img1.imgtp.com/2023/07/25/lNqXPOvs.ico', 'openai_chat_api_4', 98, 'closed', 'OpenAi Chat GPT 4', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (20, '通义千问', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'ali_tyqw', 0, 'wait_coding', '阿里 通义千问', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (21, 'POE', 'https://img1.imgtp.com/2023/07/12/1oLT69QU.png', 'poe', 0, 'wait_coding', 'POE', '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell` VALUES (22, '文心千帆 ERNIE-Bot', 'https://img1.imgtp.com/2023/05/20/xuxNAI2w.ico', 'wxqf_ernie_bot', 80, 'published', 'ERNIE-Bot 是百度自行研发的大语言模型，覆盖海量中文数据，具有更强的对话问答、内容创作生成等能力', '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell` VALUES (23, '文心千帆 ERNIE-Bot-turbo', 'https://img1.imgtp.com/2023/05/20/xuxNAI2w.ico', 'wxqf_ernie_bot_turbo', 80, 'published', 'ERNIE-Bot-turbo 是百度自行研发的大语言模型，覆盖海量中文数据，具有更强的对话问答、内容创作生成等能力，响应速度更快', '2023-07-28 01:00:00', '2023-07-28 01:00:00   ');
INSERT INTO `bh_cell` VALUES (24, '文心千帆 BLOOMZ-7B', 'https://img1.imgtp.com/2023/05/20/xuxNAI2w.ico', 'wxqf_bloomz_7b', 80, 'published', 'BLOOMZ-7B 是业内知名的大语言模型，由 Hugging Face 研发并开源，能够以 46 种语言和 13 种编程语言输出文本', '2023-07-28 01:00:00', '2023-07-28 01:00:00');

-- ----------------------------
-- Table structure for bh_cell_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_cell_config`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cell_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cell code',
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一编码，cell 中唯一',
    `default_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '默认值',
    `example_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '示例值',
    `is_required` tinyint(1) NOT NULL COMMENT '是否必填，0 否 1 是',
    `is_have_default_value` tinyint(1) NOT NULL COMMENT '是否有默认值，0 否 1 是',
    `is_user_can_use_default_value` tinyint(1) NOT NULL COMMENT '用户是否可以使用默认值，0 否 1 是',
    `is_user_visible` tinyint(1) NOT NULL COMMENT '用户是否可见，0 否 1 是',
    `is_user_value_visible` tinyint(1) NOT NULL COMMENT '用户是否可见默认值，0 否 1 是',
    `is_user_modifiable` tinyint(1) NOT NULL COMMENT '用户是否可修改，0 否 1 是',
    `is_user_live_modifiable` tinyint(1) NOT NULL COMMENT '用户创建房间后是否可修改，0 否 1 是',
    `introduce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '介绍，用户端查看',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注，管理端查看',
    `front_component_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '前端组件内容',
    `front_component_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '前端组件类型',
    `is_deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除 0 否 NULL 是',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_cell_key`(`cell_code` ASC, `code` ASC, `is_deleted` ASC) USING BTREE COMMENT 'cell 配置项编码唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'cell 配置项表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_cell_config
-- ----------------------------
INSERT INTO `bh_cell_config` VALUES (1, 'new_bing', '模式', 'mode', 'balance', '1', 1, 1, 0, 0, 1, 1, 1, '模式', '模式', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (2, 'openai_chat_api_3_5', '模型', 'model', 'gpt-3.5-turbo', 'gpt-3.5-turbo', 1, 1, 0, 0, 1, 1, 1, '模型', '模型', '', 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (3, 'openai_chat_api_3_5', '单次回复限制 max_tokens', 'max_tokens', '1000', '1000', 1, 1, 0, 1, 1, 1, 1, '每次限制的回复长度', '每次限制的回复长度', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (4, 'openai_chat_api_3_5', '随机性 temperature', 'temperature', '0.2', '0.2', 1, 1, 0, 1, 1, 1, 1, '值越大，回复越随机', '值越大，回复越随机', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (5, 'openai_chat_api_3_5', '话题新鲜度 presence_penalty', 'presence_penalty', '1', '1', 1, 1, 0, 1, 1, 1, 1, '值越大，越有可能扩展到新话题', '值越大，越有可能扩展到新话题', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (6, 'openai_chat_api_3_5', '系统消息', 'system_message', '你是 ChatGPT', '你是 ChatGPT', 1, 1, 0, 1, 1, 1, 1, '前置消息，可以决定本次对话的角色', '前置消息，可以决定本次对话的角色', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (7, 'openai_chat_api_3_5', 'api_key', 'api_key', '', 'sk-xxx', 1, 1, 0, 0, 0, 1, 1, 'api_key', 'api_key', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (8, 'openai_chat_api_3_5', '代理地址', 'openai_base_url', 'https://api.openai.com/', 'https://api.openai.com/', 1, 1, 0, 0, 0, 1, 1, '代理地址，可以用自己搭建的', '代理地址，可以用自己搭建的', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (9, 'openai_chat_api_3_5', '附带历史消息条数', 'context_count', '8', '32', 1, 1, 0, 1, 1, 1, 1, '上下文条数', '上下文条数', '{\"min\":1,\"max\":10,\"step\":1}', 'slider', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (10, 'openai_chat_api_3_5', '附带多久的历史消息', 'context_related_time_hour', '24', '0', 1, 1, 0, 1, 1, 1, 1, '单位小时，0 表示附带所有的消息', '单位小时，0 表示附带所有的消息', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (11, 'openai_chat_web_3_5', 'model', 'model', 'text-davinci-002-render-sha', 'text-davinci-002-render-sha', 1, 1, 0, 0, 1, 1, 1, '模型', '模型', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (12, 'openai_chat_web_3_5', 'access_token', 'access_token', 'Bearer xxxxx', 'Bearer xxxxx', 1, 1, 0, 1, 0, 1, 1, 'AccessToken', 'https://chat.openai.com/api/auth/session', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (13, 'openai_chat_web_3_5', 'proxy_url', 'proxy_url', 'https://xxxxxxxxxx/conversation', '代理地址', 1, 1, 0, 0, 1, 1, 1, '代理地址', '代理地址', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (14, 'openai_chat_web_4', 'model', 'model', 'gpt-4', 'gpt-4', 1, 1, 0, 0, 1, 1, 1, 'gpt-4', 'gpt-4', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (15, 'openai_chat_web_4', 'access_token', 'access_token', 'Bearer xxxxxxxxxxxx', 'Bearer xxxxx', 1, 1, 0, 1, 0, 1, 1, 'AccessToken', 'https://chat.openai.com/api/auth/session', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (16, 'openai_chat_web_4', 'proxy_url', 'proxy_url', 'https://xxxxxxxxxx/conversation', '代理地址', 1, 1, 0, 0, 1, 1, 1, '代理地址', '代理地址', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (17, 'openai_image', 'api_key', 'api_key', '', 'sk-xxxx', 1, 1, 0, 0, 1, 1, 1, 'apiKey', 'apiKey，不填走 key 池', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (18, 'openai_image', 'openai_base_url', 'openai_base_url', 'https://api.openai.com/', 'https://api.openai.com/', 1, 1, 0, 0, 1, 1, 1, '代理地址', '代理地址', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (19, 'openai_image', 'size', 'size', '256x256', '256x256', 1, 1, 0, 0, 1, 1, 1, '图片尺寸', '图片尺寸', '[{\"label\":\"256x256\",\"key\":\"256x256\"},{\"label\":\"512x512\",\"key\":\"512x512\"},{\"label\":\"256*256\",\"key\":\"1024x1024\"}]', 'dropDown', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (20, 'Midjourney', '服务器 id', 'guild_id', 'xxxxxxxxxxxx', '服务器 id', 1, 1, 0, 0, 0, 0, 0, '服务器 id', '服务器 id', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (21, 'Midjourney', '频道 id', 'channel_id', 'xxxxxxxxxxxx', '频道 id', 1, 1, 0, 0, 0, 0, 0, '频道 id', '频道 id', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (22, 'Midjourney', '用户 token', 'user_token', 'xxxxxxxxxxxx', '用户 token', 1, 1, 0, 0, 0, 0, 0, '用户 token', '用户 token', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (23, 'Midjourney', '机器人 token', 'bot_token', 'xxxxxxxxxxxx', '机器人 token', 1, 1, 0, 0, 0, 0, 0, '机器人 token', '机器人 token', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (24, 'Midjourney', '机器人名称', 'mj_bot_name', 'Midjourney Bot', '机器人名称', 1, 1, 0, 0, 0, 0, 0, '机器人名称', 'Midjourney 机器人的名称，需要一致', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (25, 'Midjourney', 'user-agent', 'user_agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36', 'user-agent', 1, 1, 0, 0, 0, 0, 0, 'user-agent', '调用 discord 接口时的 user-agent', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (27, 'Midjourney', '等待队列最大长度', 'max_wait_queue_size', '10', '等待队列最大长度', 1, 1, 0, 0, 0, 0, 0, '等待队列最大长度', '等待队列最大长度，不要太大，不然代码中设置的一些超时时间可能会出问题', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (28, 'Midjourney', '执行队列最大长度', 'max_execute_queue_size', '2', '执行队列最大长度', 1, 1, 0, 0, 0, 0, 0, '执行队列最大长度', '执行队列最大长度，不要超过 MJ 限制', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (29, 'Midjourney', '最大文件大小', 'max_file_size', '6291456', '最大文件大小', 1, 1, 0, 0, 0, 0, 0, '最大文件大小', '最大文件大小，用于 descibe 上传图片，单位字节', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (30, 'openai_chat_api_3_5', '是否启用本地敏感词库', 'enabled_local_sensitive_word', 'false', 'false', 1, 1, 1, 0, 0, 0, 0, '是否启用本地敏感词库', '是否启用本地敏感词库', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (31, 'openai_chat_api_3_5', '是否启用百度内容审核', 'enabled_baidu_aip', 'false', 'false', 1, 1, 1, 0, 0, 0, 0, '是否启用百度内容审核', '是否启用百度内容审核', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (32, 'Midjourney', '是否启用百度内容审核', 'enabled_baidu_aip', 'false', 'false', 1, 1, 1, 0, 0, 0, 0, '是否启用百度内容审核', '是否启用百度内容审核', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (33, 'Midjourney', '是否启用本地敏感词库', 'enabled_local_sensitive_word', 'false', 'false', 1, 1, 1, 0, 0, 0, 0, '是否启用本地敏感词库', '是否启用本地敏感词库', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (34, 'openai_chat_api_3_5', 'ApiKey 使用策略', 'key_strategy', 'weight', 'weight', 1, 1, 1, 0, 0, 0, 0, 'ApiKey 使用策略', 'ApiKey 使用策略', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (35, 'openai_image', 'ApiKey 使用策略', 'key_strategy', 'weight', 'weight', 1, 1, 1, 0, 0, 0, 0, 'ApiKey 使用策略', 'ApiKey 使用策略', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (36, 'openai_chat_api_4', '系统消息', 'system_message', '你是 ChatGPT', '你是 ChatGPT', 1, 1, 0, 1, 1, 1, 1, '前置消息，可以决定本次对话的角色', '前置消息，可以决定本次对话的角色', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (37, 'openai_chat_api_4', '模型', 'model', 'gpt-4', 'gpt-4', 1, 1, 0, 0, 1, 1, 1, '模型', '模型', '', 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (38, 'openai_chat_api_4', 'ApiKey 使用策略', 'key_strategy', 'weight', 'weight', 1, 1, 1, 0, 0, 0, 0, 'ApiKey 使用策略', 'ApiKey 使用策略', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (39, 'openai_chat_api_4', '是否启用百度内容审核', 'enabled_baidu_aip', 'false', 'false', 1, 1, 1, 0, 0, 0, 0, '是否启用百度内容审核', '是否启用百度内容审核', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (40, 'openai_chat_api_4', '是否启用本地敏感词库', 'enabled_local_sensitive_word', 'false', 'false', 1, 1, 1, 0, 0, 0, 0, '是否启用本地敏感词库', '是否启用本地敏感词库', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (41, 'openai_chat_api_4', '单次回复限制 max_tokens', 'max_tokens', '1000', '1000', 1, 1, 0, 1, 1, 1, 1, '每次限制的回复长度', '每次限制的回复长度', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (42, 'openai_chat_api_4', '随机性 temperature', 'temperature', '0.2', '0.2', 1, 1, 0, 1, 1, 1, 1, '值越大，回复越随机', '值越大，回复越随机', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (43, 'openai_chat_api_4', '附带多久的历史消息', 'context_related_time_hour', '24', '0', 1, 1, 0, 1, 1, 1, 1, '附带多久的历史消息', '附带多久的历史消息', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (44, 'openai_chat_api_4', '话题新鲜度 presence_penalty', 'presence_penalty', '1', '1', 1, 1, 0, 1, 1, 1, 1, '值越大，越有可能扩展到新话题', '值越大，越有可能扩展到新话题', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (45, 'openai_chat_api_4', 'api_key', 'api_key', '', 'sk-xxx', 1, 1, 0, 0, 0, 1, 1, 'apiKey', 'apiKey，不填走 key 池', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (46, 'openai_chat_api_4', '代理地址', 'openai_base_url', 'https://api.openai.com/', 'https://api.openai.com/', 1, 1, 0, 0, 0, 1, 1, '代理地址，可以用自己搭建的', '代理地址，可以用自己搭建的', NULL, 'input', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (47, 'openai_chat_api_4', '附带历史消息数', 'context_count', '8', '32', 1, 1, 0, 1, 1, 1, 1, '单位小时，0 表示附带所有的消息', '单位小时，0 表示附带所有的消息', '{\"min\":1,\"max\":10,\"step\":1}', 'slider', 0, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config` VALUES (48, 'wxqf_ernie_bot', '温度', 'temperature', '0.95', '0.95', 1, 1, 0, 1, 1, 1, 1, '温度说明：\r\n（1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定\r\n（2）默认 0.95，范围 (0, 1.0]，不能为 0\r\n（3）建议该参数和 top_p 只设置1个\r\n（4）建议 top_p 和 temperature 不要同时更改', '温度说明：\r\n（1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定\r\n（2）默认 0.95，范围 (0, 1.0]，不能为 0\r\n（3）建议该参数和 top_p 只设置1个\r\n（4）建议 top_p 和 temperature 不要同时更改', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (49, 'wxqf_ernie_bot', 'TOP_P', 'top_p', '0.95', '0.95', 1, 1, 0, 1, 1, 1, 1, '说明：\r\n（1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定\r\n（2）默认 0.95，范围 (0, 1.0]，不能为 0\r\n（3）建议该参数和 top_p 只设置1个\r\n（4）建议 top_p 和 temperature 不要同时更改', '说明：\r\n（1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定\r\n（2）默认 0.95，范围 (0, 1.0]，不能为 0\r\n（3）建议该参数和 top_p 只设置1个\r\n（4）建议 top_p 和 temperature 不要同时更改', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (50, 'wxqf_ernie_bot', '话题新鲜度 presence_penalty', 'penalty_score', '1.0', '1.0', 1, 1, 0, 1, 1, 1, 1, '通过对已生成的 token 增加惩罚，减少重复生成的现象。说明：\r\n（1）值越大表示惩罚越大 \r\n（2）默认1.0，取值范围：[1.0, 2.0]', '通过对已生成的 token 增加惩罚，减少重复生成的现象。说明：\r\n（1）值越大表示惩罚越大 \r\n（2）默认1.0，取值范围：[1.0, 2.0]', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (51, 'wxqf_ernie_bot', 'user_id', 'user_id', 'aibeehive', 'aibeehive', 1, 1, 0, 0, 0, 0, 0, '表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用', '表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (52, 'wxqf_ernie_bot', '附带历史消息条数', 'context_count', '8', '8', 1, 1, 0, 1, 1, 1, 0, '上下文条数', '上下文条数', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (53, 'wxqf_ernie_bot', '附带多久的历史消息', 'context_related_time_hour', '8', '8', 1, 1, 0, 1, 1, 1, 0, '单位小时，0 表示附带所有的消息', '单位小时，0 表示附带所有的消息', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (54, 'wxqf_ernie_bot', 'access_token', 'access_token', 'xxxxxxxxx', '8', 1, 1, 0, 0, 0, 0, 0, 'access_token', '通过 key 和 secret 生成', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (55, 'wxqf_ernie_bot', '请求地址', 'request_url', 'https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions', '8', 1, 1, 0, 0, 0, 0, 0, '请求地址', '请求地址', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (56, 'wxqf_ernie_bot_turbo', 'user_id', 'user_id', 'aibeehive', 'aibeehive', 1, 1, 0, 0, 0, 0, 0, '上下文条数', '上下文条数', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (57, 'wxqf_ernie_bot_turbo', '附带历史消息条数', 'context_count', '8', '8', 1, 1, 0, 1, 1, 1, 0, '单位小时，0 表示附带所有的消息', '单位小时，0 表示附带所有的消息', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (58, 'wxqf_ernie_bot_turbo', '附带多久的历史消息', 'context_related_time_hour', '8', '8', 1, 1, 0, 1, 1, 1, 0, '单位小时，0 表示附带所有的消息', '单位小时，0 表示附带所有的消息', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (59, 'wxqf_ernie_bot_turbo', 'access_token', 'access_token', 'xxxxxxxxx', '8', 1, 1, 0, 0, 0, 0, 0, 'access_token', '通过 key 和 secret 生成', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (60, 'wxqf_ernie_bot_turbo', '请求地址', 'request_url', 'https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant', '8', 1, 1, 0, 0, 0, 0, 0, '请求地址', '请求地址', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (61, 'wxqf_bloomz_7b', 'user_id', 'user_id', 'aibeehive', 'aibeehive', 1, 1, 0, 0, 0, 0, 0, '表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用', '表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (62, 'wxqf_bloomz_7b', '附带历史消息条数', 'context_count', '8', '8', 1, 1, 0, 1, 1, 1, 0, '上下文条数', '上下文条数', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (63, 'wxqf_bloomz_7b', '附带多久的历史消息', 'context_related_time_hour', '8', '8', 1, 1, 0, 1, 1, 1, 0, '单位小时，0 表示附带所有的消息', '单位小时，0 表示附带所有的消息', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (64, 'wxqf_bloomz_7b', 'access_token', 'access_token', 'xxxxxxxxx', '8', 1, 1, 0, 0, 0, 0, 0, 'access_token', '通过 key 和 secret 生成', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (65, 'wxqf_bloomz_7b', '请求地址', 'request_url', 'https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/bloomz_7b1', '8', 1, 1, 0, 0, 0, 0, 0, '请求地址', '请求地址', '', 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (66, 'new_bing', 'WebSocket 连接地址', 'wss_url', 'wss://sydney.bing.com/sydney/ChatHub', 'wss://sydney.bing.com/sydney/ChatHub', 1, 1, 0, 0, 0, 0, 0, 'WebSocket 连接地址', 'WebSocket 连接地址', NULL, 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (67, 'new_bing', '创建对话地址', 'create_conversation_url', 'https://www.bing.com/turing/conversation/create', 'https://www.bing.com/turing/conversation/create', 1, 1, 0, 0, 0, 0, 0, '创建对话地址', '创建对话地址', NULL, 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config` VALUES (68, 'new_bing', 'Cookie', 'cookie', 'xxxxxxxxx', 'cookie', 1, 1, 0, 0, 0, 0, 0, 'Cookie', 'Cookie', NULL, 'input', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');


-- ----------------------------
-- Table structure for bh_cell_config_permission
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_cell_config_permission`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 id',
    `cell_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cell code',
    `cell_config_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cell 配置项 code',
    `type` tinyint NOT NULL COMMENT '权限类型',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_user_cell_config`(`user_id` ASC, `cell_code` ASC, `cell_config_code` ASC, `type` ASC) USING BTREE COMMENT '用户 cell 配置项唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'cell 配置项权限' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_cell_config_permission
-- ----------------------------
INSERT INTO `bh_cell_config_permission` VALUES (1, 0, 'openai_chat_web_3_5', '0', 1, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (2, 0, 'openai_chat_api_3_5', '0', 1, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (3, 0, 'new_bing', '0', 1, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (4, 0, 'Midjourney', '0', 1, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (5, 0, 'openai_image', '0', 1, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (6, 0, 'openai_chat_api_4', '0', 1, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (7, 0, 'openai_chat_web_4', '0', 1, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (8, 0, 'wxqf_ernie_bot', '0', 1, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (9, 0, 'wxqf_ernie_bot_turbo', '0', 1, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_config_permission` VALUES (10, 0, 'wxqf_bloomz_7b', '0', 1, '2023-07-28 01:00:00', '2023-07-28 01:00:00');


-- ----------------------------
-- Table structure for bh_cell_permission
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_cell_permission`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 id',
    `cell_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cell code',
    `type` tinyint NOT NULL COMMENT '类型',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_user_cell`(`user_id` ASC, `cell_code` ASC, `type` ASC) USING BTREE COMMENT '权限关系唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'cell 权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_cell_permission
-- ----------------------------
INSERT INTO `bh_cell_permission` VALUES (1, 0, 'new_bing', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (2, 0, 'Midjourney', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (3, 0, 'openai_chat_api_3_5', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (4, 0, 'openai_chat_web_3_5', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (5, 0, 'openai_chat_web_4', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (6, 0, 'openai_image', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (7, 0, 'openai_chat_api_4', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (8, 0, 'wxqf_ernie_bot', 2, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (9, 0, 'ali_tyqw', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (10, 0, 'ChatGLM', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (11, 0, 'poe', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (12, 0, 'Bard', 2, '2023-06-16 01:00:00', '2023-06-16 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (14, 0, 'wxqf_bloomz_7b', 2, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_cell_permission` VALUES (15, 0, 'wxqf_ernie_bot_turbo', 2, '2023-07-28 01:00:00', '2023-07-28 01:00:00');


-- ----------------------------
-- Table structure for bh_email_verify_code
-- ----------------------------
DROP TABLE IF EXISTS `bh_email_verify_code`;
CREATE TABLE IF NOT EXISTS `bh_email_verify_code`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `to_email_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码接收邮箱地址',
    `verify_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
    `is_used` tinyint(1) NOT NULL COMMENT '是否使用 0 否 1 是',
    `verify_ip` char(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '核销IP，方便识别一些机器人账号',
    `expire_at` datetime NOT NULL COMMENT '验证码过期时间',
    `biz_type` tinyint NOT NULL COMMENT '当前邮箱业务',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_verify_code`(`verify_code` ASC) USING BTREE COMMENT '防止验证码重复'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邮箱验证码核销记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_email_verify_code
-- ----------------------------

-- ----------------------------
-- Table structure for bh_front_user_base
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_front_user_base`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
    `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
    `description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
    `avatar_version` int NOT NULL COMMENT '头像版本号',
    `last_login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上一次登录 IP',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '前端用户基础信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_front_user_base
-- ----------------------------
INSERT INTO `bh_front_user_base` (`id`, `nickname`, `status`, `description`, `avatar_version`, `last_login_ip`, `remark`, `create_time`, `update_time`) VALUES (1, 'hello_bee', 'normal', NULL, 0, '0:0:0:0:0:0:0:1', '默认用户', '2023-07-16 01:00:00', '2023-07-16 01:00:00');

-- ----------------------------
-- Table structure for bh_front_user_extra_binding
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_front_user_extra_binding`  (
    `id` int NOT NULL AUTO_INCREMENT,
    `binding_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '绑定类型,qq,wechat,sina,github,email,phone',
    `extra_info_id` int NOT NULL COMMENT '额外信息表ID',
    `base_user_id` int NOT NULL COMMENT '基础用户表的ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `front_user_extra_binding_pk2`(`binding_type` ASC, `base_user_id` ASC, `extra_info_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '前端用户绑定表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_front_user_extra_binding
-- ----------------------------
INSERT INTO `bh_front_user_extra_binding` (`id`, `binding_type`, `extra_info_id`, `base_user_id`, `create_time`, `update_time`) VALUES (1, 'email', 1, 1, '2023-07-16 01:00:00', '2023-07-16 01:00:00');

-- ----------------------------
-- Table structure for bh_front_user_extra_email
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_front_user_extra_email`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱账号',
    `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '加密后的密码',
    `salt` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '加密盐',
    `verified` tinyint NOT NULL DEFAULT 0 COMMENT '是否验证过，0 否 1 是',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `front_user_extra_email_pk2`(`username` ASC) USING BTREE COMMENT '邮箱唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '前端用户邮箱登录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_front_user_extra_email
-- ----------------------------
INSERT INTO `bh_front_user_extra_email` (`id`, `username`, `password`, `salt`, `verified`, `create_time`, `update_time`) VALUES (1, 'hellobee@aibeehive.icu', '2f15a70135c72f69', 'j4dlo1', 1, '2023-07-16 01:00:00', '2023-07-16 01:00:00');

-- ----------------------------
-- Table structure for bh_openai_api_key
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_openai_api_key`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'apiKey',
    `base_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求地址',
    `use_scenes` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '使用场景列表',
    `total_balance` decimal(10, 3) NOT NULL COMMENT '总额度（美元）',
    `usage_balance` decimal(10, 3) NOT NULL COMMENT '已使用额度（美元）',
    `remain_balance` decimal(10, 3) NOT NULL COMMENT '剩余额度（美元）',
    `balance_water_line` decimal(10, 3) NOT NULL COMMENT '余额水位线（美元）',
    `refresh_status_time` datetime NOT NULL COMMENT '刷新状态时间',
    `refresh_balance_time` datetime NOT NULL COMMENT '刷新余额时间',
    `is_refresh_balance` tinyint NOT NULL COMMENT '是否刷新余额',
    `is_refresh_status` tinyint NOT NULL COMMENT '是否刷新状态',
    `weight` int NOT NULL COMMENT '权重，权重高的优先执行',
    `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '备注',
    `update_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '更新理由',
    `error_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
    `version` int NOT NULL COMMENT '版本',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OpenAi ApiKey 表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_openai_api_key
-- ----------------------------
INSERT INTO `bh_openai_api_key` VALUES (1, 'sk-xxxx', 'https://api.openai.com/', '[\"GPT3.5\",\"IMAGE\"]', 0.000, 0.000, 0.000, 2.000, '2023-07-15 23:00:00', '2023-07-15 23:00:00', 1, 1, 1, 'disable', '自己填完整', '', '', 1, '2023-07-01 17:36:45', '2023-07-15 23:48:39');
INSERT INTO `bh_openai_api_key` VALUES (16, 'sk-xxxx', 'https://api.openai.com/', '[\"GPT4\"]', 0.000, 0.000, 0.000, 2.000, '2023-07-15 23:00:00', '2023-07-15 23:00:00', 1, 1, 1, 'disable', '自己填完整', '', '', 1, '2023-07-01 22:09:37', '2023-07-15 23:48:37');

-- ----------------------------
-- Table structure for bh_room
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_room`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 ID',
    `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '颜色，十六进制',
    `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
    `pin_time` bigint NOT NULL DEFAULT 0 COMMENT '固定时间戳',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ip',
    `cell_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cell code',
    `is_deleted` tinyint NOT NULL COMMENT '是否删除 0 否 1 是',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_room
-- ----------------------------

-- ----------------------------
-- Table structure for bh_room_bing
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_room_bing`  (
    `room_id` bigint NOT NULL COMMENT '房间 id',
    `user_id` int NOT NULL COMMENT '用户 id',
    `mode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bing 模式',
    `conversation_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bing conversationId',
    `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bing clientId',
    `conversation_signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bing conversationSignature',
    `max_num_user_messages_in_conversation` tinyint NOT NULL COMMENT '最大提问次数',
    `num_user_messages_in_conversation` tinyint NOT NULL COMMENT '累计提问次数',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`room_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'NewBing 房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_room_bing
-- ----------------------------

-- ----------------------------
-- Table structure for bh_room_bing_msg
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_room_bing_msg`  (
    `id` bigint NOT NULL COMMENT '主键',
    `parent_message_id` bigint NULL DEFAULT NULL COMMENT '父消息 id',
    `room_id` bigint NOT NULL COMMENT '房间 id',
    `user_id` int NOT NULL COMMENT '用户 id',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
    `type` tinyint NOT NULL COMMENT '消息类型',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
    `mode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bing 模式',
    `conversation_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bing conversationId',
    `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bing clientId',
    `conversation_signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bing conversationSignature',
    `max_num_user_messages_in_conversation` tinyint NOT NULL COMMENT '最大提问次数',
    `num_user_messages_in_conversation` tinyint NOT NULL COMMENT '累计提问次数',
    `suggest_responses` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'bing 推荐提问',
    `source_attributions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'bing 数据来源',
    `is_new_topic` tinyint NOT NULL COMMENT '是否新话题',
    `refresh_room_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '刷新房间原因',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'NewBing 房间消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_room_bing_msg
-- ----------------------------

-- ----------------------------
-- Table structure for bh_room_config_param
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_room_config_param`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 ID',
    `room_id` bigint NOT NULL COMMENT '房间 ID',
    `cell_config_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置项 code',
    `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置项值',
    `is_deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除 0 否 NULL 是',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '房间配置项参数表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_room_config_param
-- ----------------------------

-- ----------------------------
-- Table structure for bh_room_midjourney_msg
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_room_midjourney_msg`  (
    `id` bigint NOT NULL COMMENT '主键',
    `room_id` bigint NOT NULL COMMENT '房间 id',
    `user_id` int NOT NULL COMMENT '用户 id',
    `type` tinyint NOT NULL COMMENT '消息类型',
    `prompt` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户输入',
    `final_prompt` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最终的输入',
    `response_content` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '响应内容',
    `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '指令动作',
    `compressed_image_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '压缩图名称',
    `original_image_name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '原图名称',
    `uv_parent_id` bigint NULL DEFAULT NULL COMMENT 'uv 指令的父消息 id',
    `u_use_bit` int NULL DEFAULT NULL COMMENT 'u 指令使用比特位',
    `uv_index` tinyint(1) NULL DEFAULT NULL COMMENT 'uv 位置',
    `status` tinyint NOT NULL COMMENT '状态',
    `discord_finish_time` datetime NULL DEFAULT NULL COMMENT 'discord 结束时间',
    `discord_start_time` datetime NULL DEFAULT NULL COMMENT 'discord 开始时间',
    `discord_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'discord 消息 id',
    `discord_channel_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'discord 频道 id',
    `discord_image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'discord 图片地址',
    `failure_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '失败原因',
    `is_deleted` tinyint NOT NULL COMMENT '是否删除 0 否 1 是',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Midjourney 房间消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_room_midjourney_msg
-- ----------------------------

-- ----------------------------
-- Table structure for bh_room_openai_chat_msg
-- ----------------------------
DROP TABLE IF EXISTS `bh_room_openai_chat_msg`;
CREATE TABLE IF NOT EXISTS `bh_room_openai_chat_msg`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 id',
    `room_id` bigint NOT NULL COMMENT '房间 id',
    `parent_question_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级问题消息 id',
    `message_type` int NOT NULL COMMENT '消息类型枚举',
    `model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模型名称',
    `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ApiKey',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
    `original_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息的原始请求或响应数据',
    `response_error_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误的响应数据',
    `prompt_tokens` bigint NULL DEFAULT NULL COMMENT '输入消息的 tokens',
    `completion_tokens` bigint NULL DEFAULT NULL COMMENT '输出消息的 tokens',
    `total_tokens` bigint NULL DEFAULT NULL COMMENT '累计 Tokens',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
    `status` int NOT NULL COMMENT '消息状态',
    `room_config_param_json` json NULL COMMENT '房间配置项参数 json',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    `update_time` timestamp NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_parent_question_message_id`(`parent_question_message_id` ASC) USING BTREE COMMENT '父消息只能有一个子消息'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OpenAi 对话房间消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_room_openai_chat_msg
-- ----------------------------

-- ----------------------------
-- Table structure for bh_room_openai_chat_web_msg
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_room_openai_chat_web_msg`  (
    `id` bigint NOT NULL COMMENT '主键',
    `request_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求的 messageId',
    `request_conversation_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求的 conversationId',
    `request_parent_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求的 parentMessageId',
    `user_id` int NOT NULL COMMENT '用户 id',
    `room_id` bigint NOT NULL COMMENT '房间 id',
    `message_type` int NOT NULL COMMENT '消息类型枚举',
    `model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模型名称',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
    `original_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息的原始请求或响应数据',
    `response_error_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误的响应数据',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
    `status` int NOT NULL COMMENT '消息状态',
    `room_config_param_json` json NULL COMMENT '房间配置项参数 json',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    `update_time` timestamp NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OpenAi 对话 Web 房间消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_room_openai_chat_web_msg
-- ----------------------------

-- ----------------------------
-- Table structure for bh_room_openai_image_msg
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_room_openai_image_msg`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 id',
    `room_id` bigint NOT NULL COMMENT '房间 id',
    `parent_question_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级问题消息 id',
    `message_type` int NOT NULL COMMENT '消息类型枚举',
    `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ApiKey',
    `size` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '尺寸大小',
    `prompt` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输入内容',
    `openai_image_url` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'openai 图片地址',
    `image_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片名称',
    `original_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息的原始请求或响应数据',
    `response_error_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误的响应数据',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
    `status` int NOT NULL COMMENT '消息状态',
    `room_config_param_json` json NULL COMMENT '房间配置项参数 json',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    `update_time` timestamp NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_parent_question_message_id`(`parent_question_message_id` ASC) USING BTREE COMMENT '父消息只能有一个子消息'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OpenAi 图像房间消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_room_openai_image_msg
-- ----------------------------

-- ----------------------------
-- Table structure for bh_sensitive_word
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_sensitive_word`  (
      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
      `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '敏感词内容',
      `status` tinyint NOT NULL COMMENT '状态 1 启用 2 停用',
      `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 0 否 NULL 是',
      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
      PRIMARY KEY (`id`) USING BTREE,
      UNIQUE INDEX `uk_word`(`word` ASC, `is_deleted` ASC) USING BTREE COMMENT '敏感词唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敏感词表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_sensitive_word
-- ----------------------------

-- ----------------------------
-- Table structure for bh_sys_email_send_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_sys_email_send_log`  (
      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
      `from_email_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发件人邮箱',
      `to_email_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收件人邮箱',
      `biz_type` int NOT NULL COMMENT '业务类型',
      `request_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求 ip',
      `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发送内容',
      `message_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发送后会返回一个messageId',
      `status` tinyint NOT NULL COMMENT '发送状态，0失败，1成功',
      `message` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发送后的消息，用于记录成功/失败的信息，成功默认为success',
      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邮箱发送日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_sys_email_send_log
-- ----------------------------

-- ----------------------------
-- Table structure for bh_sys_front_user_login_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_sys_front_user_login_log`  (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `base_user_id` int NOT NULL COMMENT '登录的基础用户ID',
    `login_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录方式（注册方式），邮箱登录，手机登录等等',
    `login_extra_info_id` int NOT NULL COMMENT '登录信息ID与login_type有关联，邮箱登录时关联front_user_extra_email',
    `login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录的IP地址',
    `login_status` tinyint(1) NOT NULL COMMENT '登录状态，1登录成功，0登录失败',
    `message` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '结果，如果成功一律success；否则保存错误信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '前端用户登录日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_sys_front_user_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for bh_sys_param
-- ----------------------------
CREATE TABLE IF NOT EXISTS `bh_sys_param`  (
     `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
     `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
     `param_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'key',
     `param_value` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'value',
     `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注',
     `is_deleted` tinyint NULL DEFAULT NULL COMMENT '是否删除 0 否 NULL 是',
     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (`id`) USING BTREE,
     UNIQUE INDEX `uk_key`(`param_key` ASC, `is_deleted` ASC) USING BTREE COMMENT 'key 唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统参数表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bh_sys_param
-- ----------------------------
INSERT INTO `bh_sys_param` VALUES (1, '百度 AI 配置', 'baidu-aip', '{\"enabled\":false,\"appId\":\"xxxxxx\",\"appKey\":\"xxxxxx\",\"secretKey\":\"xxxxxx\"}', '百度 AI 配置', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_sys_param` VALUES (2, '邮箱注册登录配置', 'email-registerLoginConfig', '{\"registerVerificationRedirectUrl\":\"http://localhost:3200/emailValidation?type=email&verifyCode=\",\"registerVerifyCodeExpireMinutes\":15,\"registerTemplateSubject\":\"【AI 蜂巢】账号注册\",\"registerAllowSuffix\":\"*\",\"registerEnabled\":true,\"loginAllowSuffix\":\"*\",\"registerCheckEnabled\":false}', '{\r\n	\"registerVerificationRedirectUrl\": \"http://localhost:1002/#/emailValidation?type=email&verifyCode=\",\r\n	\"registerVerifyCodeExpireMinutes\": \"验证码过期时间（分钟）\",\r\n	\"registerTemplateSubject\": \"邮件标题\",\r\n	\"registerAllowSuffix\": \"@qq.com,*\",\r\n	\"registerEnabled\": true,\r\n	\"loginAllowSuffix\": \"@qq.com,*\",\r\n	\"registerCheckEnabled\": true\r\n}', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_sys_param` VALUES (3, '邮箱配置', 'email-config', '{\"host\":\"smtp.qq.com\",\"port\":465,\"from\":\"xxxxxx@qq.com\",\"user\":\"xxxxxx@qq.com\",\"pass\":\"xxxxxx\",\"auth\":true,\"sslEnable\":true,\"startttlsEnable\":false}', '{\r\n	\"host\": \"smtp.qq.com\",\r\n	\"port\": 465,\r\n	\"from\": \"xxx@qq.com\",\r\n	\"user\": \"xxx@qq.com\",\r\n	\"pass\": \"xxx\",\r\n	\"auth\": true, \r\n	\"sslEnable\": true,\r\n	\"startttlsEnable\": false\r\n}', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_sys_param` VALUES (4, '邮箱-邮箱注册模板内容', 'email-registerTemplateContent', '<!DOCTYPE html>\r\n<html xmlns:th=\"http://www.thymeleaf.org\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <title>Email Verification</title>\r\n    <style>\r\n        body {\r\n            font-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;\r\n            font-size: 14px;\r\n            line-height: 1.42857143;\r\n            color: #333;\r\n            background-color: #F3F3F3;\r\n            text-align: center;\r\n        }\r\n\r\n        .container {\r\n            background-color: #FFFFFF;\r\n            border-radius: 4px;\r\n            padding: 30px;\r\n            width: 600px;\r\n            margin: auto;\r\n        }\r\n\r\n        .btn {\r\n            display: inline-block;\r\n            padding: 6px 12px;\r\n            margin-bottom: 0;\r\n            font-size: 14px;\r\n            font-weight: 400;\r\n            line-height: 1.42857143;\r\n            text-align: center;\r\n            white-space: nowrap;\r\n            vertical-align: middle;\r\n            cursor: pointer;\r\n            -webkit-user-select: none;\r\n            -moz-user-select: none;\r\n            -ms-user-select: none;\r\n            user-select: none;\r\n            background-image: none;\r\n            border-radius: 4px;\r\n            color: #fff;\r\n            background-color: #4f99d9;\r\n            border-color: #418fd3;\r\n            text-decoration: none;\r\n        }\r\n    </style>\r\n</head>\r\n<body>\r\n    <div class=\"container\">\r\n        <h2>请验证您的邮箱地址</h2>\r\n        <p>感谢您注册我们的网站，请点击以下链接验证您的邮箱地址</p>\r\n        <p><a th:href=\"@{|${verificationUrl}|}\" class=\"btn\">点击验证邮箱地址</a></p>\r\n        <p>如果您没有注册我们的网站，请忽略此邮件。</p>\r\n        <p>有问题可以回复此邮件</p>\r\n    </div>\r\n</body>\r\n</html>', '内容太多单独放一个', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_sys_param` VALUES (5, '管理端账号', 'admin-account', 'beehive', '管理端登录时的账号', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');
INSERT INTO `bh_sys_param` VALUES (6, '管理端密码', 'admin-password', 'beehive', '管理端登录时的密码', 0, '2023-07-28 01:00:00', '2023-07-28 01:00:00');


-- ----------------------------
-- Table structure for bh_room_wxqf_chat_msg
-- ----------------------------
CREATE TABLE `bh_room_wxqf_chat_msg`  (
    `id` bigint NOT NULL COMMENT '主键',
    `user_id` int NOT NULL COMMENT '用户 id',
    `room_id` bigint NOT NULL COMMENT '房间 id',
    `parent_question_message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父级问题消息 id',
    `message_type` int NOT NULL COMMENT '消息类型枚举',
    `model_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模型名称',
    `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ApiKey',
    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
    `original_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息的原始请求或响应数据',
    `response_error_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误的响应数据',
    `prompt_tokens` bigint NULL DEFAULT NULL COMMENT '输入消息的 tokens',
    `completion_tokens` bigint NULL DEFAULT NULL COMMENT '输出消息的 tokens',
    `total_tokens` bigint NULL DEFAULT NULL COMMENT '累计 Tokens',
    `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
    `status` int NOT NULL COMMENT '消息状态',
    `room_config_param_json` json NULL COMMENT '房间配置项参数 json',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    `update_time` timestamp NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uq_parent_question_message_id`(`parent_question_message_id` ASC) USING BTREE COMMENT '父消息只能有一个子消息'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'OpenAi 对话房间消息表' ROW_FORMAT = Dynamic;
