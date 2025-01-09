package com.rect.iot.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.rect.iot.model.BuildErrors;
import com.rect.iot.model.DeviceConstants;
import com.rect.iot.model.device.Device;
import com.rect.iot.repository.BuildErrorRepo;
import com.rect.iot.repository.DeviceConstantsRepo;
import com.rect.iot.repository.DeviceRepo;

@Service
public class BuildService {
    @Autowired
    private DeviceConstantsRepo deviceConstantsRepo;
    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private BuildErrorRepo buildErrorRepo;

    @Async
    public void buildProject(String templateId, Device device, String version) throws IOException, InterruptedException {
        // Path source = Paths.get(version, null)
        String tempWorkDir = System.getProperty("user.dir") + "/temp/build-area/" + device.getId() + "/" + version + "/";
        File sourceDirectory = new File(System.getProperty("user.dir") + "/Code/" + templateId + "/" + version + "/");
        File destinationDirectory = new File(tempWorkDir);
        FileUtils.copyDirectory(sourceDirectory, destinationDirectory);

        DeviceConstants deviceConstants = deviceConstantsRepo.findByDeviceIdAndVersion(device.getId(), version);
            if (deviceConstants != null) {
            File projectConstantsFile = new File(tempWorkDir + "device_constants.h");
            if (projectConstantsFile.exists()) {
                FileUtils.delete(projectConstantsFile);
            }
            FileUtils.writeStringToFile(projectConstantsFile, deviceConstants.getData(), "UTF-8");
        }

        String error = buildProject(tempWorkDir);
        System.out.println(error);
        File buildFile = new File(tempWorkDir + ".pio/build/node32s/firmware.bin");
        if (buildFile.exists()) {
            File renamedFile = new File(tempWorkDir + ".pio/build/node32s/" + device.getId() + ".bin");
            buildFile.renameTo(renamedFile);
            File existingBuildFile = new File(System.getProperty("user.dir") + "/Uploads/" + device.getId() + ".bin");
            if (existingBuildFile.exists()) {
                existingBuildFile.delete();
            }
            FileUtils.moveFileToDirectory(renamedFile, new File(System.getProperty("user.dir") + "/Uploads/"), true);
            FileUtils.deleteDirectory(destinationDirectory);
            System.out.println("Build ok");

            device.setTargetVersion(version);
            deviceRepo.save(device);
        } else {
            buildErrorRepo.save(BuildErrors.builder()
                .templateId(templateId)
                .deviceId(device.getId())
                .errorData(error)
                .build());
        }
        
    }

     String buildProject(String dirString) throws IOException, InterruptedException {
        String[] command = new String[] { "C:\\Users\\cibik\\.platformio\\penv\\Scripts\\platformio.exe", "run", "-s" };
        File dir = new File(dirString);
        Process process = Runtime.getRuntime().exec(command, null, dir);
        return processResults(process);
    }

    public String processResults(Process process) throws IOException, InterruptedException {
        BufferedReader reader;
        if(process.waitFor() == 0) {
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } else {
            reader = new BufferedReader(process.errorReader());
        }
        // for reading errors
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line).append(System.lineSeparator());
        }
        return result.toString();
    }
}
