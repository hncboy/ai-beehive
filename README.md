# chatgpt-web-java
# 分支 official/main

## 介绍 

- [Chanzhaoyu/chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web) 项目的 java 后台
- 该分支跟随官方 main 分支联动，在不影响基本功能的情况可以对后端进行优化

## 框架

- Spring Boot 2.7.10
- JDK 17
- SpringDoc 接口文档
- [ChatGPT java sdk](https://github.com/PlexPt/chatgpt-java)

## 功能

- 待实现：配置 dockerfile
- 待实现：accessToken 接口调用，ApiKey 上下文接口调用
- 待实现：ip 限流

## 接口

| 路径          | 功能         | 完成情况                  |
| ------------- | ------------ | ------------------------- |
| /config       | 获取聊天配置 | 已完成                    |
| /chat-process | 消息处理     | ApiKey 无上写文方式已接入 |
| /verify       | 校验密码     | 已完成                    |
| /verify       | 获取模型信息 | 已完成                    |

## 地址

- 接口文档：http://localhost:3002/swagger-ui.html

