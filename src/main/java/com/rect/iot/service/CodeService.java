package com.rect.iot.service;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CodeService {
    public ResponseEntity<String> saveTemplateCode(String templateId, MultipartFile[] files) {
        for (MultipartFile file : files) {

            String filePath = System.getProperty("user.dir") + "/Code/" + templateId + "/" + file.getOriginalFilename();
            System.out.println(filePath);
            try {
                File targetFile = new File(filePath);
                targetFile.getParentFile().mkdirs(); // Create directories if they don't exist
                targetFile.createNewFile();

                FileOutputStream fout = new FileOutputStream(targetFile);
                fout.write(file.getBytes());
                fout.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok("saved");

    }
}
