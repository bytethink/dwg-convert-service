#!/bin/bash

# 编译Java应用
echo "===== 编译Java应用 ====="
./mvnw clean package -DskipTests

# 运行应用
echo "===== 运行应用 ====="
java -jar target/dwg-convert-service-0.0.1-SNAPSHOT.jar

# 注意：要使用此脚本，您需要先安装LibreDWG
# 可以使用Homebrew安装：brew install libredwg 