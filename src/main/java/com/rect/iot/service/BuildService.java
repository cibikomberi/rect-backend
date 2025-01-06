package com.rect.iot.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.rect.iot.model.DeviceConstants;
import com.rect.iot.model.device.Device;
import com.rect.iot.repository.DeviceConstantsRepo;

@Service
public class BuildService {
    @Autowired
    private DeviceConstantsRepo deviceConstantsRepo;

    @Async
    public void buildProject(String templateId, Device device, String version) throws IOException, InterruptedException {
        // Path source = Paths.get(version, null)
        String tempWorkDir = System.getProperty("user.dir") + "/temp/build-area/" + device.getId() + "/" + version + "/";
        File sourceDirectory = new File(System.getProperty("user.dir") + "/Code/" + templateId + "/" + version + "/");
        File destinationDirectory = new File(tempWorkDir);
        FileUtils.copyDirectory(sourceDirectory, destinationDirectory);

        DeviceConstants deviceConstants = deviceConstantsRepo.findByDeviceIdAndVersion(device.getId(), version);
        File projectConstantsFile = new File(tempWorkDir + "device_constants.h");
        // FileUtils.delete(projectConstantsFile);
        FileUtils.writeStringToFile(projectConstantsFile, deviceConstants.getData(), "UTF-8");

        buildProject(tempWorkDir);
    }

     int buildProject(String dirString) throws IOException, InterruptedException {
        String[] command = new String[] { "C:\\Users\\cibik\\.platformio\\penv\\Scripts\\platformio.exe", "run" };
        File dir = new File(dirString);
        // try {
        Process process = Runtime.getRuntime().exec(command, null, dir);
        printResults(process);
        return process.waitFor();
        // } catch (IOException e) {
        // return 1;
        // } catch (InterruptedException e) {
        // return 1;
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
