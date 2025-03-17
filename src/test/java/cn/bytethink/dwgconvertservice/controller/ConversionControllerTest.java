package cn.bytethink.dwgconvertservice.controller;

import cn.bytethink.dwgconvertservice.service.ConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversionController.class)
public class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversionService conversionService;

    @Test
    public void testConvertDwgToDxf() throws Exception {
        // 创建模拟DWG文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.dwg",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "DWG file content".getBytes()
        );

        // 模拟转换服务返回DXF资源
        Resource mockResource = new ByteArrayResource("DXF file content".getBytes()) {
            @Override
            public String getFilename() {
                return "test.dxf";
            }
        };
        
        when(conversionService.convertDwgToDxf(any())).thenReturn(mockResource);

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/convert/dwg-to-dxf")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.dxf\""));
    }

    @Test
    public void testConvertEmptyFile() throws Exception {
        // 创建空文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.dwg",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new byte[0]
        );

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/convert/dwg-to-dxf")
                .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testConvertInvalidFileType() throws Exception {
        // 创建非DWG文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "This is not a DWG file".getBytes()
        );

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/convert/dwg-to-dxf")
                .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testConversionError() throws Exception {
        // 创建模拟DWG文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "error.dwg",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "DWG file content".getBytes()
        );

        // 模拟转换服务抛出异常
        when(conversionService.convertDwgToDxf(any())).thenThrow(new IOException("Conversion failed"));

        // 执行请求并验证结果
        mockMvc.perform(multipart("/api/convert/dwg-to-dxf")
                .file(file))
                .andExpect(status().isInternalServerError());
    }
} 