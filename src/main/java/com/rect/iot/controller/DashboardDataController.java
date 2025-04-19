package com.rect.iot.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rect.iot.service.DashboardDataService;

import lombok.RequiredArgsConstructor;

@Controller
@CrossOrigin
@RequiredArgsConstructor
public class DashboardDataController {
    
    private final DashboardDataService dashboardDataService;

    @GetMapping("dashboard-data/{dashboardId}/{deviceId}/{datastreamId}/{range}")
    @ResponseBody
    public Object resolveDashboardData(@PathVariable String dashboardId, @PathVariable String deviceId, @PathVariable String datastreamId, @PathVariable String range) throws IllegalAccessException {
        Map<String, Object> a = new HashMap<>();
        a.put("type", range);
        a.put("data", dashboardDataService.resolveDashboardData(dashboardId, deviceId, datastreamId, range));
        return a;
    }
    
    @MessageMapping("/dashboard/post/{deviceId}/{datastreamId}")
    @SendTo("/topic/data/{deviceId}/{datastreamId}")
    public Object receiveDashboardData(@DestinationVariable String deviceId, @DestinationVariable String datastreamId, String data) {
        return dashboardDataService.receiveDashboardData(deviceId, datastreamId, data);
    }
}
