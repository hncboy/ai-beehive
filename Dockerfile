FROM maven:3.8.3-openjdk-17 AS build
# 复制项目文件到容器中
COPY . /app
# 设置工作目录
WORKDIR /app
# 构建项目
RUN mvn clean package  --settings settings.xml

FROM openjdk:17-jdk-slim
# 复制生成的 jar 文件到容器中
COPY --from=0 /app/chatgpt-bootstrap/target/*.jar /app/app.jar
# 设置工作目录
WORKDIR /app
# 暴露端口
EXPOSE 3002
# 设置时区为 Asia/Shanghai
ENV TZ=Asia/Shanghai

RUN apt-get update
# 安装字体配置包，验证码需要
RUN apt-get install -y fontconfig
# 设置容器的时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 定义启动命令
ENTRYPOINT ["sh","-c","java -jar app.jar"]