# chatgpt-web-java
# 分支 main

## 介绍 

- [Chanzhaoyu/chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web) 项目的 java 后台
- 该分支关联项目的 2.8.5 版本

## 框架

- Spring Boot 2.7.10
- JDK 17
- SpringDoc 接口文档
- MyBatis Plus
- [PlexPt ChatGPT java sdk](https://github.com/PlexPt/chatgpt-java)
- [Grt1228 ChatGPT java sdk](https://github.com/Grt1228/chatgpt-java)

## 功能

### 已实现

- 通过 h2 数据库实现聊天数据存储来实现 apiKey 方式的上下文聊天
- 聊天记录通过 h2 进行内存存储或持久化
- AccessToken 和 ApiKey 发送消息

### 待实现

- 配置 dockfile 打包
- ip 限流
- 异常信息特定封装返回

### 后续计划

- 后台管理界面

## 接口

| 路径          | 功能         | 完成情况 |
| ------------- | ------------ | -------- |
| /config       | 获取聊天配置 | 已完成   |
| /chat-process | 消息处理     | 已完成   |
| /verify       | 校验密码     | 已完成   |
| /session      | 获取模型信息 | 已完成   |

## 地址

- 接口文档：http://localhost:3002/swagger-ui.html

## 运行

- 根据 application.properties 里的配置，优先 ApiKey 方式
- 项目启动时会自动运行 h2 的建库建表 SQL，默认 file 持久化
- 目前是通过 IDEA 运行，后面配置下 Dockfile

## Docker build & Run
```shell
 docker build -t chatgpt-web-java .
 docker run -d -p 3002:3002 chatgpt-web-java
```

## 表结构

- 聊天室表

| 列名             | 数据类型                 | 约束                        | 说明                       |
| ---------------- | ------------------------ | --------------------------- | -------------------------- |
| id               | BIGINT                   | PRIMARY KEY, AUTO_INCREMENT | 主键                       |
| ip               | VARCHAR(255)             | NULL                        | ip                         |
| conversation_id  | VARCHAR(255)             | UNIQUE, NULL                | 对话 id，唯一              |
| first_message_id | VARCHAR(255)             | UNIQUE, NULL                | 第一条消息 id，唯一        |
| title            | VARCHAR(255)             | NOT NULL                    | 对话标题，从第一条消息截取 |
| api_type         | VARCHAR(20)              | NOT NULL                    | API 类型                   |
| create_time      | TIMESTAMP WITH TIME ZONE | NOT NULL                    | 创建时间                   |
| update_time      | TIMESTAMP WITH TIME ZONE | NOT NULL                    | 更新时间                   |

- 聊天记录表

| 列名                       | 数据类型                 | 约束        | 说明                     |
| -------------------------- | ------------------------ | ----------- | ------------------------ |
| message_id                 | VARCHAR(255)             | PRIMARY KEY | 消息 id                  |
| parent_message_id          | VARCHAR(255)             |             | 父级消息 id              |
| parent_answer_message_id   | VARCHAR(255)             |             | 父级回答消息 id          |
| parent_question_message_id | VARCHAR(255)             |             | 父级问题消息 id          |
| context_count              | BIGINT                   | NOT NULL    | 上下文数量               |
| question_context_count     | BIGINT                   | NOT NULL    | 问题上下文数量           |
| message_type               | INTEGER                  | NOT NULL    | 消息类型枚举             |
| chat_room_id               | BIGINT                   | NOT NULL    | 聊天室 id                |
| conversation_id            | VARCHAR(255)             | NULL        | 对话 id                  |
| api_type                   | VARCHAR(20)              | NOT NULL    | API 类型                 |
| api_key                    | VARCHAR(255)             | NULL        | ApiKey                   |
| content                    | VARCHAR(5000)            | NOT NULL    | 消息内容                 |
| original_data              | TEXT                     |             | 消息的原始请求或响应数据 |
| response_error_data        | TEXT                     |             | 错误的响应数据           |
| prompt_tokens              | BIGINT                   |             | 输入消息的 tokens        |
| completion_tokens          | BIGINT                   |             | 输出消息的 tokens        |
| total_tokens               | BIGINT                   |             | 累计 Tokens              |
| ip                         | VARCHAR(255)             |             | ip                       |
| status                     | INTEGER                  | NOT NULL    | 聊天记录状态             |
| create_time                | TIMESTAMP WITH TIME ZONE | NOT NULL    | 创建时间                 |
| update_time                | TIMESTAMP WITH TIME ZONE | NOT NULL    | 更新时间                 |
