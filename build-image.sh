#!/bin/bash

# 设置变量
IMAGE_NAME="dwg-convert-service"
IMAGE_TAG="latest"

# 确保Docker使用BuildKit
export DOCKER_BUILDKIT=1

echo "===== 开始构建 $IMAGE_NAME:$IMAGE_TAG 镜像 ====="
echo "构建可能需要一些时间，请耐心等待..."

# 创建临时目录用于构建缓存
mkdir -p .docker-cache

# 使用buildx构建多架构镜像
docker buildx create --name multiarch-builder --use || true
docker buildx inspect --bootstrap

# 构建并加载到本地Docker
echo "构建AMD64架构镜像..."
docker buildx build --platform linux/amd64 \
  --tag $IMAGE_NAME:$IMAGE_TAG \
  --load \
  --cache-from type=local,src=.docker-cache \
  --cache-to type=local,dest=.docker-cache,mode=max \
  --progress=plain \
  -f Dockerfile .

echo "===== 镜像构建完成 ====="
echo "镜像信息:"
docker images | grep $IMAGE_NAME

echo "===== 构建完成 ====="
echo "您可以使用以下命令运行容器:"
echo "docker run -p 8080:8080 $IMAGE_NAME:$IMAGE_TAG"
echo "或者使用docker-compose:"
echo "docker-compose up -d" 