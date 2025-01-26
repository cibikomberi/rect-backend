package com.rect.iot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rect.iot.controller.MqttEventListener;
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
public class DashboardDataService {

    @Autowired
    private ThingDataRepo thingDataRepo;
    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private ThingService thingService;
    @Autowired
    private DeviceMetadataRepo deviceMetadataRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MqttEventListener mqtt;
    @Autowired
    private ThingLogRepo thingLogRepo;

    // @Autowired
    // private DeviceMetadataRepo deviceMetadataRepo;
//TODO : conditional range
    public Object resolveDashboardData(String deviceId, String datastreamId, String range ) {
        if (!range.equals("last")) {
            int days = Integer.parseInt(range);
            ArrayList<ChartDataDTO> chartData = new ArrayList<>();
            if (datastreamId.equals("rect-log")) {
                System.out.println(LocalDateTime.now().minusDays(days));
                return thingLogRepo.findByDeviceIdAndTimeAfter(deviceId, LocalDateTime.now().minusDays(days));
            }
                    List<ThingData<?>> a = thingDataRepo.findByDeviceIdAndDatastreamIdAndDateTimeAfter(deviceId, datastreamId, LocalDateTime.now().minusDays(days));
                    a.stream().forEach(b -> chartData.add(ChartDataDTO.builder()
                            .dateTime(b.getDateTime())
                            .value(b.getValue())
                            .build()));
            return chartData;
        } else {
                Object a = thingDataRepo.findFirstByDeviceIdAndDatastreamIdOrderByDateTimeDesc(deviceId, datastreamId).getValue();
                return a;
        }
    }

    public Object receiveDashboardData(String deviceId, String datastreamId, String dataIn) {
        Device device = deviceRepo.findById(deviceId).get();
        DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
        List<Datastream> datastreams = metadata.getDatastreams();

        if (datastreamId.equals("rect-log")) {
            ThingLog thingLog = thingService.saveThingLog("[USER] " + dataIn, "User", deviceId);
            mqtt.sendMessage("rect/device/" + deviceId + "/command", dataIn, false);
        }
        
        if (datastreams.size() > 0) {
            int index =  metadata.getDatastreams().indexOf(Datastream.builder().identifier(datastreamId).build());
            if (index >= 0) {
                Datastream datastream = datastreams.get(index);
                Object parsedData;
                if (datastream.getType().equals("Integer")) {
                    parsedData = Integer.parseInt(dataIn);
                } else if (datastream.getType().equals("Float")) {
                    parsedData = Float.parseFloat(dataIn);
                    
                } else {
                    parsedData = dataIn;
                }
                HashMap<String, Object> payload = new HashMap<>();
                payload.put("id", datastreamId);
                payload.put("data", parsedData);
                mqtt.sendMessage("rect/device/" + deviceId + "/data", payload, false);
 
                ThingData<?> savedData = thingDataRepo.save(ThingData.builder()
                                .datastreamId(datastreamId)
                                .deviceId(deviceId)
                                .value(parsedData)
                                .dateTime(LocalDateTime.now())
                                .build());

                Map<String, Object> a = new HashMap<>();
                a.put("type", "update");
                a.put("data", ChartDataDTO.builder().value(savedData.getValue()).dateTime(savedData.getDateTime()).build());
                messagingTemplate.convertAndSend("/topic/data/" + deviceId + "/" + datastreamId, a);
            }
                            // return "ok";
        }
        return null;
    }
}
