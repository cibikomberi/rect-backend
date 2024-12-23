package com.rect.iot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.ThingData;
import com.rect.iot.model.dto.ChartDataDTO;
import com.rect.iot.model.widget.DashboardData;
import com.rect.iot.model.widget.Widget;
import com.rect.iot.repository.DashboardDataRepo;
import com.rect.iot.repository.ThingDataRepo;

@Service
public class DashboardDataService {
    @Autowired
    private DashboardDataRepo dashboardDataRepo;
    @Autowired
    private ThingDataRepo thingDataRepo;

    public Object resolveDashboardData( String dashboardId, String widgetId) {
        DashboardData data = dashboardDataRepo.findById(dashboardId).get();
        Widget widgetData = data.getWidgetData().get(widgetId);
        String widgetType = widgetData.getClass().getSimpleName();
        System.out.println(widgetType);
        if (widgetType.equals("AreaChartWidget") || widgetType.equals("LineChartWidget")) {
            ArrayList<ChartDataDTO> chartData = new ArrayList<>();
            if (widgetData.getDatastream().size() > 0) {
                List<Datastream> datastreams =  widgetData.getDatastream();
                System.out.println(datastreams);
                datastreams.stream().forEach(datastream -> {
                    List<ThingData<?>> a = thingDataRepo.findByDeviceIdAndDatastreamId(52L, datastream.getIdentifier());
                    a.stream().forEach(b -> chartData.add(ChartDataDTO.builder().group(datastream.getName())
                            .dateTime(b.getDateTime())
                            .value(b.getData())
                            .build()));
                });
            }
            return chartData;
        } else {
            if (widgetData.getDatastream().size() > 0) {
                Datastream datastream =  widgetData.getDatastream().get(0);
                System.out.println(datastream);
                Object a = thingDataRepo.findFirstByDeviceIdAndDatastreamIdOrderByDateTimeDesc(52L, datastream.getIdentifier()).getData();
                System.out.println(datastream.getIdentifier()); 
                System.out.println(a);
                return a;
            }
            return null;
        }
    }

    public Object receiveDashboardData(String dashboardId, String widgetId, String dataIn) {
        DashboardData data = dashboardDataRepo.findById(dashboardId).get();
        Widget widgetData = data.getWidgetData().get(widgetId);
        // String widgetType = widgetData.getClass().getSimpleName();
        if (widgetData.getDatastream().size() > 0) {
            Datastream datastream =  widgetData.getDatastream().get(0);
            thingDataRepo.save(ThingData.builder()
                .data(dataIn)
                .datastreamId(datastream.getIdentifier())
                .deviceId(52L)
                .dateTime(LocalDateTime.now())
                .build());
        }
        return null;
    }
}
