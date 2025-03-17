#!/bin/bash

# 停止并删除现有容器
echo "停止并删除现有容器..."
docker-compose down

# 构建新镜像
echo "构建新镜像..."
docker-compose build

# 启动服务
echo "启动服务..."
docker-compose up -d

# 显示服务状态
echo "服务状态:"
docker-compose ps

echo "服务已启动，访问 http://localhost:8080 使用 DWG 转 DXF 服务" 