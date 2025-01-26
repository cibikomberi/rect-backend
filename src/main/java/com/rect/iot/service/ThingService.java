package com.rect.iot.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.ThingData;
import com.rect.iot.model.ThingLog;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.dto.ChartDataDTO;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.ThingDataRepo;
import com.rect.iot.repository.ThingLogRepo;

@Service
public class ThingService {
    @Autowired
    private ThingDataRepo thingDataRepo;
    @Autowired
    private ThingLogRepo thingLogRepo;
    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private DeviceMetadataRepo deviceMetadataRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<String> saveThingData(String deviceId, Map<String, ?> dataMap) {
        Device device = deviceRepo.findById(deviceId).get();
        device.setLastActiveTime(LocalDateTime.now());
        deviceRepo.save(device);

        DeviceMetadata deviceMetadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
        List<Datastream> datastreams = deviceMetadata.getDatastreams();
        List<String> invalidKeys = null;

        for (Map.Entry<String, ?> set : dataMap.entrySet()) {
            List<Datastream> filteredDatastreams = datastreams.stream()
                    .filter(datastream -> datastream.getIdentifier().equals(set.getKey())).collect(Collectors.toList());
            if (filteredDatastreams.size() > 0) {
                Object data;
                try {
                    if (filteredDatastreams.getFirst().getType().equals("Integer")) {
                        data = Integer.parseInt(set.getValue().toString());
                    } else if (filteredDatastreams.getFirst().getType().equals("Float")) {
                        data = Float.parseFloat(set.getValue().toString());
                    } else {
                        data = set.getValue().toString();
                    }
                } catch (NumberFormatException e) {
                    if (invalidKeys == null) {
                        invalidKeys = new ArrayList<>();
                    }
                    invalidKeys.add(set.getKey());
                    continue;
                }
                ThingData<?> savedData = thingDataRepo.save(ThingData.builder()
                        .datastreamId(set.getKey())
                        .deviceId(deviceId)
                        .value(data)
                        .dateTime(LocalDateTime.now())
                        .build());

                Map<String, Object> a = new HashMap<>();
                a.put("type", "update");
                a.put("data", ChartDataDTO.builder().value(data).dateTime(savedData.getDateTime()).build());
                messagingTemplate.convertAndSend("/topic/data/" + deviceId + "/" + set.getKey(), a);
            } else {
                if (invalidKeys == null) {
                    invalidKeys = new ArrayList<>();
                }
                invalidKeys.add(set.getKey());
            }
        }
        return invalidKeys;
    }

    public ThingLog saveThingLog(String log, String type, String deviceId) {
        ThingLog saved = thingLogRepo.save(ThingLog.builder()
                .log(log)
                .type(type)
                .deviceId(deviceId)
                .time(LocalDateTime.now())
                .build());

        Map<String, Object> a = new HashMap<>();
                a.put("type", "update");
                a.put("data", saved);
                messagingTemplate.convertAndSend("/topic/data/" + deviceId + "/rect-log", a);

        return saved;
    }

    public ResponseEntity<?> updateThing(String deviceId, String version) throws IOException {
        Device device = deviceRepo.findById(deviceId).get();
        if (version.equals(device.getTargetVersion())) {
            if (!device.getCurrentVersion().equals(version)) {
                device.setCurrentVersion(version);
                deviceRepo.save(device);
            }
            System.out.println("up to date");
            return new ResponseEntity<>(HttpStatusCode.valueOf(304));
        }
        String filename = deviceId + ".bin";
        String fileUploadpath = System.getProperty("user.dir") + "/Uploads";

        Path fileStorageLocation = Paths.get(fileUploadpath)
                .toAbsolutePath().normalize();
        Path filePath = fileStorageLocation.resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + filename + "\"";
        long l = resource.getContentAsByteArray().length;

        // isESPDeviceUpToDate = true;
        // System.out.println("Is device up to date: " +
        // EspupdateApplication.isDeviceUpToDate);
        System.out.println("ESP updating");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header("Content-Length", String.valueOf(l))
                .body(resource);
    }
}
