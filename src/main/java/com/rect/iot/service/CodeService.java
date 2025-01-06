package com.rect.iot.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rect.iot.model.VersionControl;
import com.rect.iot.repository.VersionControlRepo;

@Service
public class CodeService {

    @Autowired
    private VersionControlRepo versionControlRepo;

    public ResponseEntity<String> saveTemplateCode(String templateId, MultipartFile[] files) throws IOException, InterruptedException {
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

        
        // buildProject(System.getProperty("user.dir") + "/Code/" + templateId + "/");
        // System.out.println("completed");
        return ResponseEntity.ok("saved");

    }

    int buildProject(String dirString) throws IOException, InterruptedException {
        String[] command = new String[] { "C:\\Users\\cibik\\.platformio\\penv\\Scripts\\platformio.exe", "run" };
        File dir = new File(dirString);
        // try {
            Process process = Runtime.getRuntime().exec(command, null, dir);
            printResults(process);
            return process.waitFor();
        // } catch (IOException e) {
        //     return 1;
        // } catch (InterruptedException e) {
        //     return 1;
        // }
    }

    public static void printResults(Process process) throws IOException, InterruptedException {
        BufferedReader reader;
        if (process.waitFor() == 0) {
            reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // process.errorReader();
        } else {
            reader = new BufferedReader(process.errorReader());
        }
        // for reading errors
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
