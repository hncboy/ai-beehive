# chatgpt-web-java

中文版 | [English](README-EN.md)

# 介绍

- [Chanzhaoyu/chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web) 项目的 Java 后台，最新代码在 main 分支
- 该分支关联项目的 [2.10.8](https://github.com/Chanzhaoyu/chatgpt-web/releases/tag/v2.10.8) 版本，在不改动前端的情况下更新后台
- 管理端开源代码 https://github.com/hncboy/chatgpt-web-admin

# 注意

### 关于提问

有问题优先通过文档和 issue 解决，也许你遇到的问题已经有解决方案了，没有的话可以提新的 issue。
### 关于 ApiKey

当前网站免费提问，因 ApiKey 额度有限，限流频率会比较高，如果有大佬赞助供网站使用的话十分感激。

# 框架

- Spring Boot 2.7.10
- JDK 17
- MySQL 8.x
- SpringDoc 接口文档
- MyBatis Plus
- MapStruct
- Lombok
- [Hutool](https://hutool.cn/) 
- [SaToken](https://sa-token.cc/) 权限校验
- [Grt1228 ChatGPT java sdk](https://github.com/Grt1228/chatgpt-java)

# 地址

- 接口文档：http://localhost:3002/swagger-ui.html
- 客户端：https://front.stargpt.top/ 密码：stargpt
- 管理端：https://admin.stargpt.top/ 账号密码 admin-admin

# 功能

## 已实现功能

### 上下文聊天

通过 MySQL 实现聊天数据存储来实现 apiKey 方式的上下文聊天，AccessToken 默认支持上下文聊天。可以通过配置参数 limitQuestionContextCount 来限制上下问问题的数量。

数据库存储了每次聊天对话的记录，在选择上下文聊天时，通过 parentMessageId 往上递归遍历获取历史消息，将历史问题以及回答消息都发送给 GPT。

![](pics/question_context_limit_test.png)

### 敏感词过滤

在项目启动时会将敏感词文件 sensitive_word_base64.txt 的数据导入到敏感词表，目前还未提供后台管理敏感词的接口，提供后这种方式可以去掉。在文件中敏感词以 base64 形式存放。并将敏感词表的数据构建到 HuTool 提供的 WordTree 类中。在发送消息调用方法判断是否属于敏感词，是的话消息发送不成功。为了兼容前端保持上下文关系，在消息内容属于敏感词的情况下会正常返回消息格式，但是带的是请求的的 conversationI 和 parentMessagId。

![](pics/sensitive_word_test.png)

### 限流

分为全局限流和 ip 限流，基于内存和双端队列实现滑动窗口限流。在限流过程会异步的将数据写入的文件中，在项目重启时会读取该文件恢复限流状态。

在配置文件中配置 maxRequest、maxRequestSecond、ipMaxRequest、ipMaxRequestSecond

![](pics/rate_limit_test.png)

## 待实现功能

- GPT 接口异常信息特定封装返回，
- 其他没发现的点

## 存在问题

- 在接口返回报错信息时，不会携带 conversationid 和 parentMessageId，导致前端下一次发送消息时会丢失这两个字段，丢失上下文关系。

# 管理端

## 消息记录

展示消息的列表，问题和回答各是一条消息。通过父消息 id 关联上一条消息。父消息和当前消息一定是同一个聊天室的。

![](pics/chat_message_1.png)

## 限流记录

查看各个 ip 的限流记录，只记录在限流时间范围的限流次数。

![](pics/rate_limit_1.png)

## 聊天室管理

查看聊天室。这里的聊天室和客户端左边的对话不是同一个概念。在同一个窗口中，我们既可以选择关联上下文发送后者不关联上下文发送。如果不关联上下文发送每次发送消息都会产生一个聊天室。

![](pics/chat_room_1.png)

## 敏感词管理

查看敏感词列表，目前只提供了查询的功能，后期可以增加管理。

![](pics/sensitive_word_1.png)

# 运行部署

## IDEA 运行

前端代码使用 WebStom、Vs Code 或者 pnpm install & dev 运行，后端 IDEA 运行。

## Docker

需要 clone 仓库并在根目录下执行

### MySQL

通过 Dockerfile_mysql  构建带有数据库表结构的镜像并运行，本地有 MySQL 可以跳过

```shell
  # 删除旧版 container （如果有的话）
  docker stop mysql_gpt && docker rm mysql_gpt
  # 构建 image
  docker build -t mysql_gpt_img:latest . -f Dockerfile_mysql
  # 运行 container
  docker run -d -p 3309:3306 \
       --name mysql_gpt \
       -v ~/mydata/mysql_dummy/data:/var/lib/mysql \
       -v  ~/mydata/mysql_dummy/conf:/etc/mysql/conf.d \
       -v ~/mydata/mysql_dummy/log:/var/log/mysql \
       mysql_gpt_img:latest
```

### Java

通过 Docker 构建 Java 应用镜像并运行

```shell
  # 删除旧版 container （如果有的话）
  docker stop chatgpt-web-java && docker rm chatgpt-web-java
  docker build -t chatgpt-web-java .
  docker run -d -p 3002:3002 chatgpt-web-java
```
如果要显式指定参数，可以在 `docker run` 后添加 `-e` 选项，配置 `application.yml` 用到的参数。例如：

```shell
  # 删除旧版 container （如果有的话）
  docker stop chatgpt-web-java && docker rm chatgpt-web-java
  docker build -t chatgpt-web-java . 
  # 如果这里要使用 java 的容器访问 mysql 容器，需要使用 host.docker.internal 而不是 localhost，才可以访问到宿主机的 3009 端口（mysql开放了3009端口）
  docker run -d -p 3002:3002 \
      -e JDBC_URL=jdbc:mysql://host.docker.internal:3309/chat?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai \
      -e MYSQL_USER_NAME=root \
      -e MYSQL_PASSWORD=123456 \
      -e CHAT_OPENAI_API_KEY=xxx \
      -e CHAT_OPENAI_ACCESS_TOKEN=xxx \
      -e CHAT_OPENAI_API_BASE_URL=http://xxx.com \
      -e CHAT_HTTP_PROXY_HOST=127.0.0.1 \
      -e CHAT_HTTP_PROXY_PORT=7890 \
      chatgpt-web-java
```
  ![](pics/docker_run.png)

## docker-compose

在 `docker-compose.yml` 文件中配置好配置后，使用 `docker-compose up -d` 可一键启动。

# 数据库表

表结构路径：`chatgpt-bootstrap/src/main/resources/db`。 不需要额外数据库的可以自行连接  H2 地址，改下连接方式就可以。


- 聊天室表
- 聊天记录表
- 敏感词表

# 联系

进群请遵守规则，禁止讨论敏感信息。

<div style="display: flex; align-items: center; gap: 20px;">
  <div style="text-align: center">
    <img style="max-width: 100%" src="pics/wechat_group.png" alt="微信" />
    <p>微信群</p>
  </div>
</div>
<div style="display: flex; align-items: center; gap: 20px;">
  <div style="text-align: center">
    <img style="max-width: 100%" src="pics/qq_group.png" alt="QQ" />
    <p>631171246</p>
  </div>
</div>
# 赞助

如果觉得项目对你有帮助的，条件允许的话可以点个 Star 或者在赞助一小点。感谢支持~

<div style="display: flex; align-items: center; gap: 20px;">
  <div style="text-align: center">
    <img style="max-width: 100%" src="pics/wechat_pay.png" alt="微信" />
    <p>微信支付</p>
  </div>
  <div style="text-align: center">
    <img style="max-width: 100%" src="pics/zhifubao_pay.png" alt="支付宝" />
    <p>支付宝</p>
  </div>
</div>
# LICENSE

MIT © [hncboy](LICENSE)
