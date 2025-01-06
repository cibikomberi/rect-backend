package com.rect.iot.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.service.CodeService;


@RestController
public class CodeController {

    @Autowired
    private CodeService codeService;
    
    @PostMapping("/code/upload/{templateId}")
    public ResponseEntity<String> saveTemplateCode(@PathVariable String templateId, @RequestPart(name = "files") MultipartFile[] files) throws IOException, InterruptedException {
        System.out.println(templateId);
        System.out.println("code uploading");
        System.out.println(files);
        return codeService.saveTemplateCode(templateId, files);
    }
    
}
