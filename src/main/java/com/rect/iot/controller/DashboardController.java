package com.rect.iot.controller;

import org.springframework.web.bind.annotation.RestController;

import com.rect.iot.model.widget.DashboardData;
import com.rect.iot.service.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@CrossOrigin
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("/dashboard/{deviceId}")
    public DashboardData saveDashboardData(@PathVariable Long deviceId, @RequestBody DashboardData data) {
        return dashboardService.saveDashboardData(deviceId, data);
    }

    @GetMapping("/dashboard/{id}")
    public DashboardData getDashboardData(@PathVariable String id) {
        return dashboardService.getDashboardData(id);
    }

    // @GetMapping("/dashboard/data")
    // public String serveDashboard(@RequestParam String param) {
    //     return new String();
    // }
    
    
    
}
