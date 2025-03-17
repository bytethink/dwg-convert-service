package cn.bytethink.dwgconvertservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "dwg-convert-service");
        response.put("timestamp", System.currentTimeMillis());
        
        // 检查LibreDWG是否可用
        boolean libredwgAvailable = checkLibreDWGAvailability();
        response.put("libredwg", libredwgAvailable ? "available" : "unavailable");
        
        if (!libredwgAvailable) {
            return ResponseEntity.status(503).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    private boolean checkLibreDWGAvailability() {
        try {
            Process process = new ProcessBuilder("dwg2dxf", "--version")
                    .redirectErrorStream(true)
                    .start();
            
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
} 