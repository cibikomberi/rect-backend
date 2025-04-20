package com.rect.iot.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.rect.iot.model.BuildJob;
import com.rect.iot.model.device.BuildErrors;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceConstants;
import com.rect.iot.repository.BuildErrorRepo;
import com.rect.iot.repository.BuildJobRepo;
import com.rect.iot.repository.DeviceConstantsRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildService {
    private final DeviceConstantsRepo deviceConstantsRepo;
    private final BuildErrorRepo buildErrorRepo;
    private final BuildJobRepo buildJobRepo;
    private final MqttMessageSender mqttMessageSender;

    @Async
    public void buildProject(String templateId, Device device, String version, String enviroinment, boolean autoDeploy) {
        buildProject(templateId, device, version, enviroinment);
        if (autoDeploy) {
            mqttMessageSender.sendMessage("rect/" + device.getId() + "/ota", version, true);
        }
    }
    private void buildProject(String templateId, Device device, String version, String enviroinment) {
        // Path source = Paths.get(version, null)
        String tempWorkDir = System.getProperty("user.dir") + "/temp/build-area/" + device.getId() + "/" + version
                + "/";
        File sourceDirectory = new File(System.getProperty("user.dir") + "/code/" + templateId + "/" + version + "/");
        File destinationDirectory = new File(tempWorkDir);
        try {
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);

            DeviceConstants deviceConstants = deviceConstantsRepo.findByDeviceIdAndVersion(device.getId(), version);
            File projectConstantsFile = new File(tempWorkDir + "src/device_constants.h");
            if (deviceConstants != null) {
                if (projectConstantsFile.exists()) {
                    FileUtils.delete(projectConstantsFile);
                }
                FileUtils.writeStringToFile(projectConstantsFile,
                        "#define RECT_DEVICE_ID \"" + device.getId() +
                                "\"\n#define RECT_API_KEY \"" + device.getApiKey() +
                                "\"\n#define RECT_DEVICE_VERSION \"" + version + "\"\n" +
                                deviceConstants.getData(),
                        "UTF-8");
            }
            FileUtils.writeStringToFile(projectConstantsFile,
                    "#define RECT_DEVICE_ID \"" + device.getId() +
                            "\"\n#define RECT_API_KEY \"" + device.getApiKey() +
                            "\"\n#define RECT_DEVICE_VERSION \"" + version + "\"",
                    "UTF-8");

            String error = buildProject(tempWorkDir);
            File buildFile = new File(tempWorkDir + ".pio/build/" + enviroinment + "/firmware.bin");
            if (buildFile.exists()) {
                File renamedFile = new File(tempWorkDir + ".pio/build/" + enviroinment + "/" + device.getId() + ".bin");
                buildFile.renameTo(renamedFile);
                File existingBuildFile = new File(
                        System.getProperty("user.dir") + "/uploads/" + device.getId() + ".bin");
                if (existingBuildFile.exists()) {
                    existingBuildFile.delete();
                }
                FileUtils.moveFileToDirectory(renamedFile, new File(System.getProperty("user.dir") + "/uploads/"),
                        true);

                updateBuildStatus(templateId, device.getId(), "Success", true);
            } else {
                updateBuildStatus(templateId, device.getId(), "Error: Unable to finish build for this environment\n" + error, false);

                
            }
        } catch (FileNotFoundException e) {
            updateBuildStatus(templateId, device.getId(), "Error: Source file not found", false);
        } catch (IOException e) {
            updateBuildStatus(templateId, device.getId(), "Error: Unable to read or write files", false);
        } catch (InterruptedException e) {
            updateBuildStatus(templateId, device.getId(), "Error: Unable to finish build", false);
        } finally {
            try {
                FileUtils.deleteDirectory(destinationDirectory);
            } catch (IOException e) {

            }
        }
    }

    synchronized void updateBuildStatus(String templateId, String deviceId, String val, boolean success) {
        BuildJob buildJob = buildJobRepo.findById(templateId).get();
        if (!success) {
            buildErrorRepo.save(BuildErrors.builder()
                            .templateId(templateId)
                            .deviceId(deviceId)
                            .errorData(val)
                            .build());
            buildJob.getDevices().put(deviceId, "Error");
        } else {
            buildJob.getDevices().put(deviceId, "Success");
        }
        buildJobRepo.save(buildJob);

    }

    String buildProject(String dirString) throws IOException, InterruptedException {
        String[] command = new String[] { "C:\\Users\\cibik\\.platformio\\penv\\Scripts\\platformio.exe", "run", "-s" };
        File dir = new File(dirString);
        Process process = Runtime.getRuntime().exec(command, null, dir);
        return processResults(process);
    }

    public String processResults(Process process) throws IOException, InterruptedException {
        BufferedReader reader;
        if (process.waitFor() == 0) {
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
