FROM --platform=linux/amd64 ubuntu:22.04 AS build

# 安装基本工具和Java
RUN apt-get update && apt-get install -y \
    openjdk-11-jdk \
    maven \
    git \
    build-essential \
    autoconf \
    libtool \
    pkg-config \
    texinfo \
    libxml2-dev \
    && rm -rf /var/lib/apt/lists/*

# 克隆并编译LibreDWG
WORKDIR /tmp
RUN git clone https://github.com/LibreDWG/libredwg.git \
    && cd libredwg \
    && ./autogen.sh \
    && ./configure \
    && make \
    && make install \
    && ldconfig

# 构建阶段 - 编译Java应用
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 运行阶段
FROM --platform=linux/amd64 ubuntu:22.04

# 安装Java运行时和LibreDWG依赖
RUN apt-get update && apt-get install -y \
    openjdk-11-jre \
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
ENTRYPOINT ["java", "-jar", "app.jar"] 