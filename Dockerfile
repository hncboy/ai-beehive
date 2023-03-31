FROM openjdk:17-jdk-slim
# 复制生成的 jar 文件到容器中
COPY chatgpt-bootstrap/target/chatgpt-bootstrap-0.0.1-SNAPSHOT.jar /app/app.jar
# 设置工作目录
WORKDIR /app
# 暴露端口
EXPOSE 3002
# 设置时区为 Asia/Shanghai
ENV TZ=Asia/Shanghai
# 设置容器的时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 定义启动命令
CMD ["java", "-jar", "app.jar"]
#CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]