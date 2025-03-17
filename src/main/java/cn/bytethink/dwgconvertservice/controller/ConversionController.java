package cn.bytethink.dwgconvertservice.controller;

import cn.bytethink.dwgconvertservice.service.ConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/convert")
@RequiredArgsConstructor
public class ConversionController {

    private final ConversionService conversionService;

    @PostMapping("/dwg-to-dxf")
    public ResponseEntity<Resource> convertDwgToDxf(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".dwg")) {
            return ResponseEntity.badRequest().build();
        }

        // 执行转换
        Resource dxfResource = conversionService.convertDwgToDxf(file);

        // 设置响应头
        String dxfFilename = originalFilename.substring(0, originalFilename.lastIndexOf('.')) + ".dxf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dxfFilename + "\"")
                .body(dxfResource);
    }
} 