package com.rect.iot.controller;

import org.springframework.web.bind.annotation.RestController;

import com.rect.iot.model.Dashboard;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.widget.DashboardData;
import com.rect.iot.service.DashboardService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@CrossOrigin
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

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
    public Dashboard getDashboardData(@PathVariable String dashboardId) throws IllegalAccessException {
        return dashboardService.getDashboardData(dashboardId);
    }

    @GetMapping("/dashboard/datastreams/{dashboardId}")
    public List<Datastream> getDatastreams(@PathVariable String dashboardId) throws IllegalAccessException {
        return dashboardService.getDatastreams(dashboardId);
    }   

    @PutMapping("/dashboard/data/{dashboardId}")
    public DashboardData updateDashboardData(@PathVariable String dashboardId, @RequestBody DashboardData data) throws IllegalAccessException {
        return dashboardService.updateDashboardData(dashboardId, data);
    }

    // @GetMapping("/dashboard/data")
    // public String serveDashboard(@RequestParam String param) {
    //     return new String();
    // }
    
    
    
}
