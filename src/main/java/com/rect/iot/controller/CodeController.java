package com.rect.iot.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.service.CodeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CodeController {

    private final CodeService codeService;
    
    @PostMapping("/code/upload/{templateId}")
    public ResponseEntity<String> saveTemplateCode(@PathVariable String templateId, @RequestPart(name = "files") MultipartFile[] files) throws IOException, InterruptedException, IllegalAccessException {
        return codeService.saveTemplateCode(templateId, files);
    }
    
}
