# chatgpt-web-java

[中文版](./README.md) | English

# Introduction

- The Java backend of the [Chanzhaoyu/chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web) project, with the latest code on the main branch.
- This branch is associated with version [2.10.8](https://github.com/Chanzhaoyu/chatgpt-web/releases/tag/v2.10.8) of the project and updates the background without modifying the front-end.
- Open source code for the management interface: https://github.com/hncboy/chatgpt-web-admin.

# Note

### About asking questions

If you have any questions, please refer to the documentation and issues first. Perhaps the problem you encountered already has a solution. If not, you can create a new issue.

### About ApiKey

The current website provides free inquiries. Due to the limited quota of ApiKey, the traffic control frequency will be relatively high. If someone would kindly sponsor the website's use, it would be much appreciated.

# Framework

- Spring Boot 2.7.10
- JDK 17
- MySQL 8.x
- SpringDoc Interface Document
- MyBatis Plus
- MapStruct
- Lombok
- [Hutool](https://hutool.cn/) 
- [SaToken](https://sa-token.cc/) Authentication Check
- [Grt1228 ChatGPT java sdk](https://github.com/Grt1228/chatgpt-java)

# Address

- Interface document: http://localhost:3002/swagger-ui.html
- Client: https://front.stargpt.top/ Password: stargpt
- Management end: https://admin.stargpt.top/ Account password admin-admin

# Function

## Implemented Functions

### Context Chatting

By using MySQL to store chat data and implementing context chatting through apiKey, AccessToken supports context chatting by default. You can limit the number of questions asked in the context by configuring the parameter limitQuestionContextCount.

The database stores the record of each chat dialogue. When selecting context chatting, it recursively traverses through parentMessageId to get historical messages and sends both the historical question and its corresponding answer message to GPT.

![](pics/question_context_limit_test.png)

### Sensitive Word Filtering

When the project starts, the data in the sensitive_word_base64.txt file is imported into the sensitive word table. Currently, there is no interface for managing sensitive words on the backend, which will be provided in the future. The sensitive words in the file are stored in base64 format. And the data of the sensitive word table is constructed into the WordTree class provided by HuTool. Call the method to judge whether the message belongs to a sensitive word when sending a message. If it does, the message will not be sent successfully. In order to maintain the context relationship with the front-end, if the message content belongs to sensitive words, the message format will be returned normally, but with the conversationI and parentMessagId of the request.

![](pics/sensitive_word_test.png)）

### Rate Limit

There are global rate limit and IP rate limit, which are implemented based on memory and double-ended queue to achieve sliding window rate limit. During the rate limiting process, the data will be asynchronously written to a file, and the rate limiting status will be restored by reading the file when the project is restarted.

Configure maxRequest, maxRequestSecond, ipMaxRequest, ipMaxRequestSecond in the configuration file.

![](pics/rate_limit_test.png)

## To-be-implemented Features

- Specific encapsulation of GPT interface error messages returned,
- Other undiscovered points

## Existing Problems

- When returning error information from the interface, it does not carry conversationid and parentMessageId, causing the front-end to lose these two fields when sending messages next time, losing context relationship.

# Management End

## Message Record

Display the list of messages, with one message for each question and answer. Associate the previous message through the parent message id. The parent message and the current message must be in the same chat room.

![](pics/chat_message_1.png)

## Rate Limit Record

View the rate limit records of each IP, only recording the number of rate limits within the rate limit time range.

![](pics/rate_limit_1.png)

## Chat Room Management

View chat rooms. The chat room here is not the same concept as the dialogue on the left side of the client. In the same window, we can choose to associate with the context or not. If we do not associate with the context, every time we send a message, a chat room will be created.

![](pics/chat_room_1.png)

## Sensitive Word Management

View the list of sensitive words. Currently, only query functions are provided, and management can be added later.

![](pics/sensitive_word_1.png)

# Deployment

### Run

#### Required Applications to Prepare Beforehand

1. For front-end code, please refer to the start-up process of project [Chanzhaoyu/chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web).

2. A local MySQL instance with port 3309 needs to be prepared beforehand. If not, a Docker MySQL container can be built using Dockerfile_mysql:
```shell
  # Remove old containers (if any)
  docker stop mysql_gpt && docker rm mysql_gpt
  # Build image
  docker build -t mysql_gpt_img:latest . -f Dockerfile_mysql
  # Run container
  docker run -d -p 3309:3306 \
       --name mysql_gpt \
       -v ~/mydata/mysql_dummy/data:/var/lib/mysql \
       -v  ~/mydata/mysql_dummy/conf:/etc/mysql/conf.d \
       -v ~/mydata/mysql_dummy/log:/var/log/mysql \
       mysql_gpt_img:latest
```

Then start by using the ChatGptApplication class under chatgpt-bootstrap.

Here are also instructions for building Java application images.

```shell
  # Remove old containers (if any)
  docker stop chatgpt-web-java && docker rm chatgpt-web-java
  docker build -t chatgpt-web-java .
  docker run -d -p 3002:3002 chatgpt-web-java
```

If there is a need to explicitly specify MySQL and chat-gpt parameters, you can add the -e option after docker run to configure the parameters used in application.yml. For example:

```shell
  # Remove old containers (if any)
  docker stop chatgpt-web-java && docker rm chatgpt-web-java
  docker build -t chatgpt-web-java . 
  # If you want to use the Java container to access the MySQL container, you need to use host.docker.internal instead of localhost to access the 3009 port on the host (mysql has opened the 3009 port)
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
### Run with docker-compose
After configuring the chat-gpt configuration in docker-compose.yml, use docker-compose up -d to start.


# Database Table
Table structure path: chatgpt-bootstrap/src/main/resources/db. If an additional database is not required, then H2 address can be connected by changing the connection method.

- Chat Room Table
- Chat Record Table
- Sensitive Word Table
# Contact
Please abide by the rules when joining the group, and discussion of sensitive information is strictly prohibited.

<div style="display: flex; align-items: center; gap: 20px;">
<div style="text-align: center">
<img style="max-width: 100%" src="pics/wechat_group.png" alt="WeChat" />
<p>WeChat Group</p>
</div>
</div>
<div style="display: flex; align-items: center; gap: 20px;">
<div style="text-align: center">
<img style="max-width: 100%" src="pics/qq_group.png" alt="QQ" />
<p>631171246</p>
</div>
</div>

# Sponsor
If you find the project helpful and conditions allow, please either star the repository or sponsor a small amount. Thank you for your support~

<div style="display: flex; align-items: center; gap: 20px;">
<div style="text-align: center">
<img style="max-width: 100%" src="pics/wechat_pay.png" alt="WeChat Pay" />
<p>WeChat Pay</p>
</div>
<div style="text-align: center">
<img style="max-width: 100%" src="pics/zhifubao_pay.png" alt="Alipay" />
<p>Alipay</p>
</div>
</div>

LICENSE
MIT © [hncboy](LICENSE)