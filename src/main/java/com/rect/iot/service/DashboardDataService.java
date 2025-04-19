package com.rect.iot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.dashboard.Dashboard;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.dto.ChartDataDTO;
import com.rect.iot.model.thing.ThingData;
import com.rect.iot.repository.DashboardRepo;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.ThingDataRepo;
import com.rect.iot.repository.ThingLogRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardDataService {

    private final ThingDataRepo thingDataRepo;
    private final DeviceRepo deviceRepo;
    private final ThingService thingService;
    private final DeviceMetadataRepo deviceMetadataRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final ThingLogRepo thingLogRepo;
    private final DashboardRepo dashboardRepo;
    private final DashboardService dashboardService;
    private final MqttMessageSender mqttMessageSender;

    public Object resolveDashboardData(String dashboardId, String deviceId, String datastreamId, String range ) throws IllegalAccessException {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        String access = dashboardService.getAccessLevel(dashboard);
        if (!(dashboard.getAccess().equals("Public") || access.equals("Viewer") || access.equals("Editor") || access.equals("Owner"))) {
            throw new IllegalAccessException("User does not have access to this dashboard");
        }
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
            thingService.saveThingLog("[USER] " + dataIn, "User", deviceId);
            mqttMessageSender.sendMessage("rect/device/" + deviceId + "/command", dataIn, false);
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
                mqttMessageSender.sendMessage("rect/device/" + deviceId + "/data", payload, false);
 
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
                System.out.println("sent");
            }
                            // return "ok";
        }
        return null;
    }
}
