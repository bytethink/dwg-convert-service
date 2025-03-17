package cn.bytethink.dwgconvertservice.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ConversionService {
    
    /**
     * 将DWG文件转换为DXF文件
     * 
     * @param dwgFile DWG文件
     * @return 转换后的DXF文件资源
     * @throws IOException 如果文件处理过程中发生错误
     */
    Resource convertDwgToDxf(MultipartFile dwgFile) throws IOException;
} 