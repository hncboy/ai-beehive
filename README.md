# chatgpt-web-java
# 分支 official/main

## 介绍 

- [Chanzhaoyu/chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web) 项目的 java 后台
- 该分支跟随官方 main 分支联动，在不影响基本功能的情况可以对后端进行优化

## 框架

- Spring Boot 2.7.10
- JDK 17
- SpringDoc 接口文档
- [PlexPt ChatGPT java sdk](https://github.com/PlexPt/chatgpt-java)
- [Grt1228 ChatGPT java sdk](https://github.com/Grt1228/chatgpt-java)

## 功能

- 待实现：配置 dockerfile
- 待实现：ApiKey 上下文方式接入
- 待实现：ip 限流
- 已实现：ApiKey 无上下文发送消息，AccessToken 发送消息

## 接口

| 路径          | 功能         | 完成情况                   |
| ------------- | ------------ | -------------------------- |
| /config       | 获取聊天配置 | 已完成                     |
| /chat-process | 消息处理     | 剩余 ApiKey 上下文方式接入 |
| /verify       | 校验密码     | 已完成                     |
| /session      | 获取模型信息 | 已完成                     |

## 地址

- 接口文档：http://localhost:3002/swagger-ui.html

## 使用方式

- 根据 application.properties 里的配置，优先 ApiKey 方式

