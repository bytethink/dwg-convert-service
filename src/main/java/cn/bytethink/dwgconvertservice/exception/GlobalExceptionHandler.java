package cn.bytethink.dwgconvertservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.error("File size exceeds maximum allowed size", exc);
        Map<String, String> response = new HashMap<>();
        response.put("message", "文件大小超过允许的最大值");
        response.put("error", "file_too_large");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleIOException(IOException exc) {
        log.error("Error processing file", exc);
        Map<String, String> response = new HashMap<>();
        response.put("message", "文件处理错误: " + exc.getMessage());
        response.put("error", "file_processing_error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception exc) {
        log.error("Unexpected error", exc);
        Map<String, String> response = new HashMap<>();
        response.put("message", "服务器内部错误");
        response.put("error", "internal_server_error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 