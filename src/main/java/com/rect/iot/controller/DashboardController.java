package com.rect.iot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.dashboard.Dashboard;
import com.rect.iot.model.dashboard.DashboardData;
import com.rect.iot.model.device.Device;
import com.rect.iot.service.DashboardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboards") 
    public List<Dashboard> getMyDashboards() {
        return dashboardService.getMyDashboards();
    }

    @GetMapping("/dashboard/{id}")
    public Dashboard getDashboardInfo(@PathVariable String id) throws IllegalAccessException {
        return dashboardService.getDashboardInfo(id);
    }

    @PostMapping("/dashboard")
    public Dashboard createDashboard(@RequestBody Dashboard dashboard) {
        return dashboardService.createDashboard(dashboard);
    }    

    @GetMapping("/dashboard/data/{dashboardId}")
    public Dashboard getDashboardData(@PathVariable String dashboardId, @RequestParam String type) throws IllegalAccessException {
        return dashboardService.getDashboardData(dashboardId, type);
    }

    @GetMapping("/dashboard/datastreams/{dashboardId}")
    public List<Datastream> getDatastreams(@PathVariable String dashboardId) throws IllegalAccessException {
        return dashboardService.getDatastreams(dashboardId);
    }
    
    @GetMapping("/dashboard/devices/{dashboardId}")
    public List<Device> getDevices(@PathVariable String dashboardId) throws IllegalAccessException {
        return dashboardService.getDevices(dashboardId);
    }

    @PutMapping("/dashboard/data/{dashboardId}")
    public DashboardData updateDashboardData(@PathVariable String dashboardId, @RequestBody DashboardData data, @RequestParam String type) throws IllegalAccessException {
        return dashboardService.updateDashboardData(dashboardId, data, type);
    }

    @GetMapping("/dashboards/shared")
    public List<Dashboard> getSharedTemplates() {
        return dashboardService.getSharedDashboards();
    }

    // @GetMapping("/dashboard/data")
    // public String serveDashboard(@RequestParam String param) {
    //     return new String();
    // }
    
    
    
}
