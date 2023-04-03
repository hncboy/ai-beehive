FROM hncboy/chatgpt-web-java:latest

# 设置工作目录
#WORKDIR /app
## 暴露端口
#EXPOSE 3002
## 设置时区为 Asia/Shanghai
#ENV TZ=Asia/Shanghai
## 设置容器的时区
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#
## 定义启动命令
#ENTRYPOINT ["sh","-c","java", "-jar", "app.jar"]
