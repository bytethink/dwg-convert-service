# DWG 转 DXF 服务

这是一个基于 Spring Boot 和 LibreDWG 的 DWG 到 DXF 文件转换服务。该服务提供了一个简单的 REST API，允许用户上传 DWG 文件并获取转换后的 DXF 文件。

## 功能特点

- 通过 HTTP API 上传 DWG 文件并转换为 DXF 格式
- 使用 LibreDWG 进行高质量的文件转换
- 容器化部署，易于安装和运行
- 支持大文件上传（默认最大 50MB）
- 跨平台支持（x86/AMD64 架构）

## 系统要求

- Docker
- 或者 Java 11+ 和 LibreDWG

## 快速开始

### 使用 Docker（推荐）

#### 构建 Docker 镜像

```bash
# 使用提供的构建脚本
./build-image.sh
```

#### 运行服务

```bash
# 使用 Docker 运行
docker run -p 8080:8080 dwg-convert-service:latest

# 或者使用 docker-compose
docker-compose up -d
```

服务将在 http://localhost:8080 上运行。

### 本地运行（开发环境）

#### 前提条件

- Java 11+
- LibreDWG（可以通过 Homebrew 安装：`brew install libredwg`）

#### 构建和运行

```bash
# 使用提供的本地运行脚本
./run-local.sh
```

## API 使用

### 转换 DWG 到 DXF

**请求**

```
POST /api/convert/dwg-to-dxf
Content-Type: multipart/form-data
```

参数:
- `file`: DWG 文件 (必需)

**响应**

成功时，服务器将返回转换后的 DXF 文件，HTTP 状态码为 200。

**示例**

使用 curl:

```bash
curl -X POST -F "file=@example.dwg" http://localhost:8080/api/convert/dwg-to-dxf -o output.dxf
```

### 健康检查

**请求**

```
GET /api/health
```

**响应**

```json
{
  "status": "UP",
  "service": "dwg-convert-service",
  "timestamp": 1647852123456,
  "libredwg": "available"
}
```

## 错误处理

服务可能返回以下错误:

- `400 Bad Request`: 请求无效，例如未提供文件或文件格式不正确
- `413 Payload Too Large`: 文件大小超过允许的最大值
- `500 Internal Server Error`: 服务器内部错误，例如转换过程中出现问题
- `503 Service Unavailable`: LibreDWG 不可用

## 技术栈

- Spring Boot 2.7.x
- LibreDWG
- Docker

## 构建多架构镜像

本项目支持构建适用于 x86/AMD64 架构的 Docker 镜像，即使在 ARM 架构（如 M1/M2 Mac）上构建也能在 x86 服务器上运行。

## 许可证

[MIT](LICENSE)

---

[English Version](README.md) 