#!/bin/bash

# 设置变量
IMAGE_NAME="dwg-convert-service"
IMAGE_TAG="latest"

echo "===== 开始快速构建 $IMAGE_NAME:$IMAGE_TAG 镜像 ====="
echo "此脚本跳过LibreDWG编译，仅用于测试Java应用构建"

# 创建临时的测试Dockerfile
cat > Dockerfile.test << EOF
FROM --platform=linux/amd64 eclipse-temurin:11-jdk AS build

# 复制pom.xml
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# 构建Java应用
COPY src ./src
RUN mvn package -DskipTests

# 运行阶段
FROM --platform=linux/amd64 eclipse-temurin:11-jre

# 复制编译好的应用
WORKDIR /app
COPY --from=build /app/target/dwg-convert-service-*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF

# 构建测试镜像
docker build -t $IMAGE_NAME:test -f Dockerfile.test .

echo "===== 测试镜像构建完成 ====="
echo "注意：此镜像不包含LibreDWG，仅用于测试Java应用构建"
echo "镜像信息:"
docker images | grep $IMAGE_NAME

# 清理临时文件
rm Dockerfile.test

echo "===== 测试构建完成 =====" 