package com.rect.iot.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Dashboard;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.widget.DashboardData;
import com.rect.iot.model.widget.Widget;
import com.rect.iot.repository.DashboardDataRepo;
import com.rect.iot.repository.DashboardRepo;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;

@Service
public class DashboardService {
    @Autowired
    private DashboardDataRepo dashboardDataRepo;
    @Autowired
    private DashboardRepo dashboardRepo;
    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private DeviceMetadataRepo deviceMetadataRepo;

    public List<Dashboard> getDashboards() {
        return dashboardRepo.findAll();
    }

    public Dashboard getDashboard(String id) {
        return dashboardRepo.findById(id).get();
    }

    public Dashboard createDashboard(Dashboard dashboard) {
        DashboardData data = DashboardData.builder()
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())  
                .build();
        DashboardData savedDashboardData = dashboardDataRepo.save(data);

        dashboard.setDashboardDataId(savedDashboardData.getId());
        return dashboardRepo.save(dashboard);
    }
    
    public DashboardData getDashboardData(String id) {
        return dashboardDataRepo.findById(id).get();
    }

    public List<Datastream> getDatastreams(String dashboardId) {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        List<Datastream> datastreams = new ArrayList<>();

        for (Long deviceId : dashboard.getAssociatedDevices()) {
            Device device = deviceRepo.findById(deviceId).get();
            DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
            metadata.getDatastreams().forEach(datastream -> datastream.setDeviceName(device.getName()));
            datastreams.addAll(metadata.getDatastreams());
        }

        return datastreams;
    }

    public DashboardData updateDashboardData(String dashboardDataId, DashboardData data) {
        return dashboardDataRepo.save(DashboardData.builder()
                .id(dashboardDataId)
                .layout(data.getLayout())
                .widgetData(data.getWidgetData())
                .build());
    }
}
