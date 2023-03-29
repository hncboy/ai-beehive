FROM maven:3.8.3-openjdk-17
# 复制项目文件到容器中
COPY . /app
# 设置工作目录
WORKDIR /app
# 构建项目
RUN mvn clean package  --settings settings.xml

FROM openjdk:17-jdk-slim
# 复制生成的jar文件到容器中
COPY --from=0 /app/chatgpt-bootstrap/target/*.jar /app/app.jar
# 设置工作目录
WORKDIR /app
EXPOSE 3002
# 定义启动命令
CMD ["java", "-jar", "app.jar"]