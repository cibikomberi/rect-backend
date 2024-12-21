package com.rect.iot.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.model.Edge;
import com.rect.iot.model.Flow;
import com.rect.iot.repository.FlowRepo;
import com.rect.iot.service.compile.ArduinoHeaderParser;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TestController {

    @Autowired
    FlowRepo flowRepo;


    @GetMapping("/test")
    public String getMethodName() {
        Edge e1 = new Edge();
        Edge e2 = new Edge();
        e1.setId("2L");
        e2.setId("4L");
        List<Edge> e = new ArrayList<>();
        e.add(e2);
        e.add(e1);
        Flow flow = Flow.builder().edges(e).build();
        flowRepo.save(flow);
        return new String("ok");
    }

@PostMapping("/parse")
    public Map<String, List<Map<String, Object>>> parseArduinoFile(@RequestParam("file") MultipartFile file) throws Exception {
        // Save uploaded file temporarily
        String tempDir = System.getProperty("java.io.tmpdir");
        File uploadedFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(uploadedFile);

        ArduinoHeaderParser parser = new ArduinoHeaderParser();

        Map<String, List<Map<String, Object>>> result;
        if (uploadedFile.getName().endsWith(".zip")) {
            // Extract ZIP and parse each .h file
            String outputDir = tempDir + "/arduino_lib/";
            new File(outputDir).mkdirs();
            new ArduinoHeaderParser().extractZipFile(uploadedFile.getPath(), outputDir);

            result = new HashMap<>();
            result.put("methods", new ArrayList<>());
            result.put("constants", new ArrayList<>());

            // Parse all header files
            Files.walk(Paths.get(outputDir))
                    .filter(path -> path.toString().endsWith(".h"))
                    .forEach(path -> {
                        try {
                            Map<String, List<Map<String, Object>>> parsed = parser.parseHeaderFile(path.toString());
                            result.get("methods").addAll(parsed.get("methods"));
                            // result.get("constants").addAll(parsed.get("constants"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

        } else if (uploadedFile.getName().endsWith(".h")) {
            result = parser.parseHeaderFile(uploadedFile.getPath());
        } else {
            throw new IllegalArgumentException("Unsupported file type!");
        }

        return result;
    }
}
