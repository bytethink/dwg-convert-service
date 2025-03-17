package cn.bytethink.dwgconvertservice.service.impl;

import cn.bytethink.dwgconvertservice.service.ConversionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ConversionServiceImpl implements ConversionService {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Override
    public Resource convertDwgToDxf(MultipartFile dwgFile) throws IOException {
        // 创建临时文件
        String originalFilename = dwgFile.getOriginalFilename();
        String fileBaseName = originalFilename != null 
                ? originalFilename.substring(0, originalFilename.lastIndexOf('.'))
                : UUID.randomUUID().toString();
        
        // 创建临时目录
        Path tempDir = Files.createTempDirectory("dwg_convert_");
        
        // 保存上传的DWG文件
        Path dwgFilePath = tempDir.resolve(fileBaseName + ".dwg");
        dwgFile.transferTo(dwgFilePath.toFile());
        
        // 设置输出DXF文件路径
        Path dxfFilePath = tempDir.resolve(fileBaseName + ".dxf");
        
        try {
            // 调用LibreDWG的dwg2dxf命令行工具进行转换
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "dwg2dxf",
                    dwgFilePath.toString(),
                    dxfFilePath.toString()
            );
            
            Process process = processBuilder.start();
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                throw new IOException("DWG to DXF conversion timed out");
            }
            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new IOException("DWG to DXF conversion failed with exit code: " + exitCode);
            }
            
            // 检查DXF文件是否生成
            File dxfFile = dxfFilePath.toFile();
            if (!dxfFile.exists() || dxfFile.length() == 0) {
                throw new IOException("DXF file was not generated");
            }
            
            return new FileSystemResource(dxfFile);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("DWG to DXF conversion was interrupted", e);
        } finally {
            // 注册JVM关闭钩子来清理临时文件
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    Files.deleteIfExists(dwgFilePath);
                    Files.deleteIfExists(dxfFilePath);
                    Files.deleteIfExists(tempDir);
                } catch (IOException e) {
                    log.error("Failed to delete temporary files", e);
                }
            }));
        }
    }
} 