package com.rect.iot.service;

import com.rect.iot.controller.EmailSender;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.automation.Automation;
import com.rect.iot.model.automation.EmailStatusAutomation;
import com.rect.iot.model.automation.EmailValueAutomation;
import com.rect.iot.model.automation.StateAutomation;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.dto.ChartDataDTO;
import com.rect.iot.model.thing.ThingData;
import com.rect.iot.model.thing.ThingLog;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.ThingDataRepo;
import com.rect.iot.repository.ThingLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThingService {

    private final ThingDataRepo thingDataRepo;
    private final ThingLogRepo thingLogRepo;
    private final DeviceRepo deviceRepo;
    private final DeviceMetadataRepo deviceMetadataRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final MqttMessageSender mqttMessageSender;
    private final EmailSender emailSender;


    public List<String> saveThingData(String deviceId, Map<String, ?> dataMap, boolean isDataFromDevice) {
        Device device = deviceRepo.findById(deviceId).get();

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

                Map<String, Object> payload = new HashMap<>();
                payload.put("type", "update");
                payload.put("data", ChartDataDTO.builder().value(data).dateTime(savedData.getDateTime()).build());
                messagingTemplate.convertAndSend("/topic/data/" + deviceId + "/" + set.getKey(), payload);
                if (isDataFromDevice) {
                    for (Automation automation : deviceMetadata.getAutomations()) {
                        if (automation instanceof StateAutomation) {
                            StateAutomation stateAutomation = (StateAutomation) automation;
                            if (stateAutomation.getDatastream().getIdentifier().equals(set.getKey())) {
                                HashMap<String, Object> val = new HashMap<>();
                                val.put("id", stateAutomation.getTargetDatastream().getIdentifier());
                                val.put("data", data);
                                mqttMessageSender.sendMessage(
                                        "rect/device/" + stateAutomation.getTargetDevice().getId() + "/data",
                                        val, false);
                            }
                        } else if (automation instanceof EmailValueAutomation) {
                            EmailValueAutomation emailValueAutomation = (EmailValueAutomation) automation;
                            if (emailValueAutomation.getDatastream().getIdentifier().equals(set.getKey())) {
                                emailSender.send(device.getName(), set.getKey(), set.getValue().toString(),
                                        emailValueAutomation.getEmailList());
                            }
                        }
                    }
                }
            } else {
                if (invalidKeys == null) {
                    invalidKeys = new ArrayList<>();
                }
                invalidKeys.add(set.getKey());
            }
        }
        return invalidKeys;
    }

    public void syncDeviceWithServer(String deviceId, List<String> datastreams) {
        // Device device = deviceRepo.findById(deviceId).get();
        List<ThingData<?>> data = thingDataRepo.findLatestValuesForDatastreams(deviceId, datastreams);
        for(ThingData<?> val: data) {
            HashMap<String, Object> payload = new HashMap<>();
            payload.put("id", val.getDatastreamId());
            payload.put("data", val.getValue());
            mqttMessageSender.sendMessage("rect/device/" + deviceId + "/data", payload, false);
        }
    }

    public void updateThingStatus(String deviceId, Map<String, ?> dataMap) {
        Device device = deviceRepo.findById(deviceId).get();
        DeviceMetadata deviceMetadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
        
        for (Automation automation : deviceMetadata.getAutomations()) {
            if (automation instanceof EmailValueAutomation) {
                EmailStatusAutomation emailStatusAutomation = (EmailStatusAutomation) automation;
                emailSender.send(device.getName(), "Status: ", dataMap.get("status").toString(),
                        emailStatusAutomation.getEmailList());
            }
        }

        device.setLastActiveTime(LocalDateTime.now());
        device.setStatus(dataMap.get("status").toString());
        deviceRepo.save(device);
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
            return new ResponseEntity<>(HttpStatusCode.valueOf(304));
        }
        String filename = deviceId + ".bin";
        String fileUploadpath = System.getProperty("user.dir") + "/uploads";

        Path fileStorageLocation = Paths.get(fileUploadpath)
                .toAbsolutePath().normalize();
        Path filePath = fileStorageLocation.resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + filename + "\"";
        long l = resource.getContentAsByteArray().length;

        // isESPDeviceUpToDate = true;
        // EspupdateApplication.isDeviceUpToDate);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header("Content-Length", String.valueOf(l))
                .body(resource);
    }
}
