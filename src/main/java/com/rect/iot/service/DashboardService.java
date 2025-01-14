package com.rect.iot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Dashboard;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.User;
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
    @Autowired
    private UserService userService;

    public List<Dashboard> getMyDashboards() {
        String userId = userService.getMyUserId();
        return dashboardRepo.findByOwner(userId).stream().filter(dashboard -> !dashboard.getIsDeviceSpecific()).toList();
    }

    public Dashboard getDashboardInfo(String id) throws IllegalAccessException {
        Dashboard dashboard = dashboardRepo.findById(id).get();
        String access = getAccessLevel(dashboard);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            return dashboardRepo.findById(id).get();
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public Dashboard createDashboard(Dashboard newDashboard) {
        String userId = userService.getMyUserId();
        DashboardData data = DashboardData.builder()
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();
        DashboardData savedDashboardData = dashboardDataRepo.save(data);

        Dashboard dashboard = new Dashboard();

        dashboard.setName(newDashboard.getName());
        dashboard.setAccess(newDashboard.getAccess());
        dashboard.setIsDeviceSpecific(false);
        dashboard.setAssociatedDevices(newDashboard.getAssociatedDevices());
        dashboard.setDashboardDataId(savedDashboardData.getId());
        dashboard.setOwner(userId);
        dashboard.setUserAccess(new HashMap<>());
        return dashboardRepo.save(dashboard);
    }

    public Dashboard getDashboardData(String dashboardId) throws IllegalAccessException {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        String access = getAccessLevel(dashboard);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            dashboard.setDashboardData(dashboardDataRepo.findById(dashboard.getDashboardDataId()).get());
            return dashboard;
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public List<Datastream> getDatastreams(String dashboardId) throws IllegalAccessException {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        String access = getAccessLevel(dashboard);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            List<Datastream> datastreams = new ArrayList<>();

            for (String deviceId : dashboard.getAssociatedDevices()) {
                System.out.println(deviceId);
                Device device = deviceRepo.findById(deviceId).get();
                DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();
                metadata.getDatastreams().forEach(datastream -> datastream.setDeviceName(device.getName()));
                datastreams.addAll(metadata.getDatastreams());
            }
            return datastreams;
        }
        throw new IllegalAccessException("User does not have access to this device");
    }
    //TODO: verify datastreams in data
    public DashboardData updateDashboardData(String dashboardId, DashboardData data) throws IllegalAccessException {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        String access = getAccessLevel(dashboard);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            return dashboardDataRepo.save(DashboardData.builder()
                    .id(dashboard.getDashboardDataId())
                    .layout(data.getLayout())
                    .widgetData(data.getWidgetData())
                    .build());
        }
        throw new IllegalAccessException("User does not have access to this device");
    }

    public List<Dashboard> getSharedDashboards() {
        User user = userService.whoAmI();
        List<Dashboard> dashboards = dashboardRepo.findAllById(user.getSharedDashboards());
        dashboards.stream().forEach(dashboard -> dashboard.setMyAccess(getAccessLevel(dashboard)));
        return dashboards;
    }

    public boolean hasViewAccess(String dashboardId, String userId) {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        return hasViewAccess(dashboard, userId);
    }
    public boolean hasViewAccess(Dashboard dashboard, String userId) {
        if (dashboard.getOwner().equals(userId)) {
            return true;
        }
        return dashboard.getUserAccess().get(userId) != null;
    }

    private String getAccessLevel(Dashboard dashboard) {
        String userId = userService.getMyUserId();

        if (dashboard.getOwner().equals(userId)) {
            return "Owner";
        }
        return dashboard.getUserAccess().getOrDefault(userId, "No Access");
    }
}
