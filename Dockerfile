FROM --platform=linux/amd64 ubuntu:22.04 AS build

# 设置环境变量以避免交互式提示
ENV DEBIAN_FRONTEND=noninteractive

# 安装基本工具和Java
RUN apt-get update && apt-get install -y \
    openjdk-11-jdk \
    maven \
    wget \
    && rm -rf /var/lib/apt/lists/*

# 下载预编译的LibreDWG二进制文件
WORKDIR /tmp
RUN apt-get update && apt-get install -y \
    libxml2-dev \
    && rm -rf /var/lib/apt/lists/*

# 下载并安装预编译的LibreDWG
RUN mkdir -p /usr/local/lib && \
    wget -q https://github.com/LibreDWG/libredwg/releases/download/0.13.3.7539/libredwg-0.13.3.7539.tar.xz -O libredwg.tar.gz && \
    tar -xzf libredwg.tar.gz -C /usr/local && \
    rm libredwg.tar.gz && \
    ldconfig

# 先复制pom.xml以利用Maven缓存
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# 构建阶段 - 编译Java应用
COPY src ./src
RUN mvn package -DskipTests

# 运行阶段 - 使用更小的基础镜像
FROM --platform=linux/amd64 eclipse-temurin:11-jre

# 安装LibreDWG运行时依赖
RUN apt-get update && apt-get install -y \
    libxml2 \
    && rm -rf /var/lib/apt/lists/*

# 从构建阶段复制LibreDWG
COPY --from=build /usr/local/bin/dwg2dxf /usr/local/bin/
COPY --from=build /usr/local/lib/libredwg* /usr/local/lib/

# 更新库缓存
RUN ldconfig

# 复制编译好的应用
WORKDIR /app
COPY --from=build /app/target/dwg-convert-service-*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"] 