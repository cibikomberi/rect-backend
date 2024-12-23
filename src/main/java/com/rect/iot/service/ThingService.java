package com.rect.iot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.ThingData;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.ThingDataRepo;

@Service
public class ThingService {
    @Autowired
    private ThingDataRepo thingDataRepo;
    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private DeviceMetadataRepo deviceMetadataRepo;

    public List<String> logData(Long deviceId, Map<String, ?> dataMap) {
        Device device = deviceRepo.findById(deviceId).get();
        device.setLastActiveTime(LocalDateTime.now());
        deviceRepo.save(device);

        DeviceMetadata deviceMetadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
        List<Datastream> datastreams = deviceMetadata.getDatastreams();
        List<String> invalidKeys = null;

        for (Map.Entry<String, ?> set : dataMap.entrySet()) {
            List<Datastream> filteredDatastreams = datastreams.stream().filter(datastream -> datastream.getIdentifier().equals(set.getKey())).collect(Collectors.toList());
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
                thingDataRepo.save(ThingData.builder()
                        .datastreamId(set.getKey())
                        .deviceId(deviceId)
                        .data(data)
                        .dateTime(LocalDateTime.now())
                        .build());
            } else {
                if (invalidKeys == null) {
                    invalidKeys = new ArrayList<>();
                }
                invalidKeys.add(set.getKey());
            }
        }
        return invalidKeys;  
    }
}
