# DWG to DXF Conversion Service

A Spring Boot and LibreDWG based service for converting DWG files to DXF format. This service provides a simple REST API that allows users to upload DWG files and receive the converted DXF files.

## Features

- Convert DWG files to DXF format via HTTP API
- High-quality conversion using LibreDWG
- Containerized deployment for easy installation and operation
- Support for large file uploads (default max 50MB)
- Cross-platform support (x86/AMD64 architecture)

## Requirements

- Docker
- Or Java 11+ and LibreDWG

## Quick Start

### Using Docker (Recommended)

#### Build Docker Image

```bash
# Use the provided build script
./build-image.sh
```

#### Run the Service

```bash
# Run with Docker
docker run -p 8080:8080 dwg-convert-service:latest

# Or use docker-compose
docker-compose up -d
```

The service will be available at http://localhost:8080.

### Local Development

#### Prerequisites

- Java 11+
- LibreDWG (can be installed via Homebrew: `brew install libredwg`)

#### Build and Run

```bash
# Use the provided local run script
./run-local.sh
```

## API Usage

### Convert DWG to DXF

**Request**

```
POST /api/convert/dwg-to-dxf
Content-Type: multipart/form-data
```

Parameters:
- `file`: DWG file (required)

**Response**

On success, the server will return the converted DXF file with HTTP status code 200.

**Example**

Using curl:

```bash
curl -X POST -F "file=@example.dwg" http://localhost:8080/api/convert/dwg-to-dxf -o output.dxf
```

### Health Check

**Request**

```
GET /api/health
```

**Response**

```json
{
  "status": "UP",
  "service": "dwg-convert-service",
  "timestamp": 1647852123456,
  "libredwg": "available"
}
```

## Error Handling

The service may return the following errors:

- `400 Bad Request`: Invalid request, such as no file provided or incorrect file format
- `413 Payload Too Large`: File size exceeds the allowed maximum
- `500 Internal Server Error`: Server-side error, such as issues during conversion
- `503 Service Unavailable`: LibreDWG is not available

## Technology Stack

- Spring Boot 2.7.x
- LibreDWG
- Docker

## Multi-Architecture Image Building

This project supports building Docker images for x86/AMD64 architecture, ensuring that images built on ARM architecture (like M1/M2 Mac) can run on x86 servers.

## License

[MIT](LICENSE)

---

[中文版说明](README_CN.md)