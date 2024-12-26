package com.rect.iot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.ThingData;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.dto.ChartDataDTO;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.ThingDataRepo;

@Service
public class DashboardDataService {

    @Autowired
    private ThingDataRepo thingDataRepo;
    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private DeviceMetadataRepo deviceMetadataRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    // @Autowired
    // private DeviceMetadataRepo deviceMetadataRepo;

    public Object resolveDashboardData(Long deviceId, String datastreamId, String range ) {
        // DashboardData data = dashboardDataRepo.findById(dashboardId).get();
        // Widget widgetData = data.getWidgetData().get(widgetId);
        // String widgetType = widgetData.getClass().getSimpleName();
        // System.out.println(widgetType);
        if (!range.equals("last")) {
            ArrayList<ChartDataDTO> chartData = new ArrayList<>();
            // Device device = deviceRepo.findById(deviceId).get();
            // DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            // metadata.
            // if (widgetData.getDatastream().size() > 0) {
            //     List<Datastream> datastreams =  widgetData.getDatastream();
            //     System.out.println(datastreams);
            //     datastreams.stream().forEach(datastream -> {
                    List<ThingData<?>> a = thingDataRepo.findByDeviceIdAndDatastreamId(deviceId, datastreamId);
                    a.stream().forEach(b -> chartData.add(ChartDataDTO.builder()
                            .dateTime(b.getDateTime())
                            .value(b.getValue())
                            .build()));
                // });
            // }
            return chartData;
        } else {
            // if (widgetData.getDatastream().size() > 0) {
                // Datastream datastream =  widgetData.getDatastream().get(0);
                // System.out.println(datastream);
                Object a = thingDataRepo.findFirstByDeviceIdAndDatastreamIdOrderByDateTimeDesc(deviceId, datastreamId).getValue();
                System.out.println(datastreamId); 
                System.out.println(a);
                return a;
            // }
            // return null;
        }
    }

    public Object receiveDashboardData(Long deviceId, String datastreamId, String dataIn) {
        Device device = deviceRepo.findById(deviceId).get();
        DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
        List<Datastream> datastreams = metadata.getDatastreams();
        
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
