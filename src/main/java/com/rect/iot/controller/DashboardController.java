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
    public List<Dashboard> getDashboards() {
        return dashboardService.getDashboards();
    }

    @GetMapping("/dashboard/{id}")
    public Dashboard getDashboard(@PathVariable String id) {
        return dashboardService.getDashboard(id);
    }

    @PostMapping("/dashboard")
    public Dashboard createDashboard(@RequestBody Dashboard dashboard) {
        return dashboardService.createDashboard(dashboard);
    }

    @GetMapping("/dashboard/data/{dashboardDataId}")
    public DashboardData getDashboardData(@PathVariable String dashboardDataId) {
        return dashboardService.getDashboardData(dashboardDataId);
    }

    @GetMapping("/dashboard/datastreams/{dashboardId}")
    public List<Datastream> getDatastreams(@PathVariable String dashboardId) {
        return dashboardService.getDatastreams(dashboardId);
    }   

    @PutMapping("/dashboard/data/{dashboardDataId}")
    public DashboardData updateDashboardData(@PathVariable String dashboardDataId, @RequestBody DashboardData data) {
        return dashboardService.updateDashboardData(dashboardDataId, data);
    }

    // @GetMapping("/dashboard/data")
    // public String serveDashboard(@RequestParam String param) {
    //     return new String();
    // }
    
    
    
}
