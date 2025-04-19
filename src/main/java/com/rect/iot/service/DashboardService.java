package com.rect.iot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.dashboard.Dashboard;
import com.rect.iot.model.dashboard.DashboardData;
import com.rect.iot.model.dashboard.Widget;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.user.User;
import com.rect.iot.repository.DashboardDataRepo;
import com.rect.iot.repository.DashboardRepo;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardDataRepo dashboardDataRepo;
    private final DashboardRepo dashboardRepo;
    private final DeviceRepo deviceRepo;
    private final DeviceMetadataRepo deviceMetadataRepo;
    private final UserService userService;

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
        throw new IllegalAccessException("User does not have access to this dashboard");
    }

    public Dashboard createDashboard(Dashboard newDashboard) {
        String userId = userService.getMyUserId();
        DashboardData data = DashboardData.builder()
                .days("1")
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();
        DashboardData mobileData = DashboardData.builder()
                .days("1")
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();
        DashboardData tabletData = DashboardData.builder()
                .days("1")
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();
        DashboardData largeData = DashboardData.builder()
                .days("1")
                .layout(new ArrayList<>())
                .widgetData(new HashMap<String, Widget>())
                .build();

        DashboardData dashboardData = dashboardDataRepo.save(data);
        DashboardData mobileDashboardData = dashboardDataRepo.save(mobileData);
        DashboardData tabletDashboardData = dashboardDataRepo.save(tabletData);
        DashboardData largeDashboardData = dashboardDataRepo.save(largeData);

        Dashboard dashboard = new Dashboard();

        dashboard.setName(newDashboard.getName());
        dashboard.setAccess(newDashboard.getAccess());
        dashboard.setIsDeviceSpecific(false);
        dashboard.setAssociatedDevices(newDashboard.getAssociatedDevices());

        dashboard.setDashboardDataId(dashboardData.getId());
        dashboard.setMobileDashboardDataId(mobileDashboardData.getId());
        dashboard.setTabletDashboardDataId(tabletDashboardData.getId());
        dashboard.setLargeDashboardDataId(largeDashboardData.getId());

        dashboard.setOwner(userId);
        dashboard.setUserAccess(new HashMap<>());
        return dashboardRepo.save(dashboard);
    }

    public Dashboard getDashboardData(String dashboardId, String type) throws IllegalAccessException {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        String access = getAccessLevel(dashboard);
        if (dashboard.getAccess().equals("Public") || access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            if (type.equals("Mobile: 600px")) {
                dashboard.setDashboardData(dashboardDataRepo.findById(dashboard.getMobileDashboardDataId()).get());
            } else if (type.equals("Tablet: 768px")) {
                dashboard.setDashboardData(dashboardDataRepo.findById(dashboard.getTabletDashboardDataId()).get());
            } else if (type.equals("Desktop: 1024px")) {
                dashboard.setDashboardData(dashboardDataRepo.findById(dashboard.getDashboardDataId()).get());
            } else if (type.equals("Large: 1440px")) {
                dashboard.setDashboardData(dashboardDataRepo.findById(dashboard.getLargeDashboardDataId()).get());
            }

            return dashboard;
        }
        throw new IllegalAccessException("User does not have access to this dashboard");
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
                metadata.getDatastreams().forEach(datastream -> {
                    datastream.setDeviceId(deviceId);
                    datastream.setDeviceName(device.getName());
                });
                datastreams.addAll(metadata.getDatastreams());
            }
            return datastreams;
        }
        throw new IllegalAccessException("User does not have access to this dashboard");
    }

    public List<Device> getDevices(String dashboardId) throws IllegalAccessException {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        String access = getAccessLevel(dashboard);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
            return deviceRepo.findAllById(dashboard.getAssociatedDevices());
        }
        throw new IllegalAccessException("User does not have access to this dashboard");
    }

    public DashboardData updateDashboardData(String dashboardId, DashboardData data, String type) throws IllegalAccessException {
        Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
        String access = getAccessLevel(dashboard);
        if (access.equals("Viewer") || access.equals("Editor") || access.equals("Owner")) {
        String dashboardDataId = "";
            if (type.equals("Mobile: 600px")) {
                dashboardDataId = dashboard.getMobileDashboardDataId();
            } else if (type.equals("Tablet: 768px")) {
                dashboardDataId = dashboard.getTabletDashboardDataId();
            } else if (type.equals("Desktop: 1024px")) {
                dashboardDataId = dashboard.getDashboardDataId();
            } else if (type.equals("Large: 1440px")) {
                dashboardDataId = dashboard.getLargeDashboardDataId();
            }

            return dashboardDataRepo.save(DashboardData.builder()
                    .id(dashboardDataId)
                    .days(data.getDays())
                    .layout(data.getLayout())
                    .widgetData(data.getWidgetData())
                    .build());
        }
        throw new IllegalAccessException("User does not have access to this dashboard");
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

    public String getAccessLevel(Dashboard dashboard) {
        String userId = userService.getMyUserId();

        if (dashboard.getOwner().equals(userId)) {
            return "Owner";
        }
        return dashboard.getUserAccess().getOrDefault(userId, "No Access");
    }
}
