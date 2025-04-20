package com.rect.iot.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.model.template.Template;
import com.rect.iot.repository.TemplateRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeService {
    private final TemplateService templateService;
    private final TemplateRepo templateRepo;

    public ResponseEntity<String> saveTemplateCode(String templateId, MultipartFile[] files)
            throws IOException, InterruptedException, IllegalAccessException {
        Template template = templateRepo.findById(templateId).get();
        String access = templateService.getAccessLevel(template);
        if (access.equals("Editor") || access.equals("Owner")) {
            String basePath = System.getProperty("user.dir") + "/code/" + templateId + "/" + template.getDevVersion() + "/";
            deleteFolder(new File(basePath));
            for (MultipartFile file : files) {
                String filePath = basePath + file.getOriginalFilename();
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
        throw new IllegalAccessException("User does not have access to this template");
    }

   

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { // Check if it's a directory
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
}
