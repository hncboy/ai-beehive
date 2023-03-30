-- 聊天室表
CREATE TABLE IF NOT EXISTS chat_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ip VARCHAR(255) NULL,
    conversation_id VARCHAR(255) UNIQUE NULL,
    first_chat_message_id BIGINT UNIQUE NOT NULL,
    first_message_id VARCHAR(255) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    api_type VARCHAR(20) NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL
);
COMMENT ON COLUMN chat_room.id IS '主键';
COMMENT ON COLUMN chat_room.ip IS 'ip';
COMMENT ON COLUMN chat_room.conversation_id IS '对话 id，唯一';
COMMENT ON COLUMN chat_room.first_message_id IS '第一条消息';
COMMENT ON COLUMN chat_room.title IS '对话标题，从第一条消息截取';
COMMENT ON COLUMN chat_room.api_type IS 'API 类型';
COMMENT ON COLUMN chat_room.create_time IS '创建时间';
COMMENT ON COLUMN chat_room.update_time IS '更新时间';

-- 聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT PRIMARY KEY,
    message_id VARCHAR(255) UNIQUE NOT NULL,
    parent_message_id VARCHAR(255),
    parent_answer_message_id VARCHAR(255),
    parent_question_message_id VARCHAR(255),
    context_count BIGINT NOT NULL,
    question_context_count BIGINT NOT NULL,
    message_type INTEGER NOT NULL,
    chat_room_id BIGINT NOT NULL,
    conversation_id VARCHAR(255) NULL,
    api_type VARCHAR(20) NOT NULL,
    api_key VARCHAR(255) NULL,
    content VARCHAR(5000) NOT NULL,
    original_data TEXT,
    response_error_data TEXT,
    prompt_tokens BIGINT,
    completion_tokens BIGINT,
    total_tokens BIGINT,
    ip VARCHAR(255) NULL,
    status INTEGER NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL
);

COMMENT ON COLUMN chat_message.id IS '主键';
COMMENT ON COLUMN chat_message.message_id IS '消息 id';
COMMENT ON COLUMN chat_message.parent_message_id IS '父级消息 id';
COMMENT ON COLUMN chat_message.parent_answer_message_id IS '父级回答消息 id';
COMMENT ON COLUMN chat_message.parent_question_message_id IS '父级问题消息 id';
COMMENT ON COLUMN chat_message.context_count IS '上下文数量';
COMMENT ON COLUMN chat_message.question_context_count IS '问题上下文数量';
COMMENT ON COLUMN chat_message.message_type IS '消息类型枚举';
COMMENT ON COLUMN chat_message.chat_room_id IS '聊天室 id';
COMMENT ON COLUMN chat_message.conversation_id IS '对话 id';
COMMENT ON COLUMN chat_message.api_type IS 'API 类型';
COMMENT ON COLUMN chat_message.api_key IS 'ApiKey';
COMMENT ON COLUMN chat_message.content IS '消息内容';
COMMENT ON COLUMN chat_message.original_data IS '消息的原始请求或响应数据';
COMMENT ON COLUMN chat_message.response_error_data IS '错误的响应数据';
COMMENT ON COLUMN chat_message.prompt_tokens IS '输入消息的 tokens';
COMMENT ON COLUMN chat_message.completion_tokens IS '输出消息的 tokens';
COMMENT ON COLUMN chat_message.total_tokens IS '累计 Tokens';
COMMENT ON COLUMN chat_message.ip IS 'ip';
COMMENT ON COLUMN chat_message.status IS '聊天记录状态';
COMMENT ON COLUMN chat_message.create_time IS '创建时间';
COMMENT ON COLUMN chat_message.update_time IS '更新时间';

-- 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_word (
    -- 主键
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- 敏感词内容
    word VARCHAR(255) NOT NULL,
    -- 创建时间
    create_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    -- 更新时间
    update_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- 状态 1 启用 2 停用
    status INTEGER NOT NULL,
    -- 是否删除 0 否 NULL 是
    is_deleted INTEGER DEFAULT 0
);