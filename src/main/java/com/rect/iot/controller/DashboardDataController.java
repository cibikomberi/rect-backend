package com.rect.iot.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.rect.iot.service.DashboardDataService;

@Controller
public class DashboardDataController {
    @Autowired
    private DashboardDataService dashboardDataService;

    @MessageMapping("/dashboard/get/{deviceId}/{datastreamId}")
    @SendTo("/topic/data/{deviceId}/{datastreamId}")
    public Object resolveDashboardData(@DestinationVariable String deviceId, @DestinationVariable String datastreamId, String range, StompHeaderAccessor headerAccessor) {
        Map<String, Object> a = new HashMap<>();
        a.put("type", range);
        a.put("data", dashboardDataService.resolveDashboardData(deviceId, datastreamId, range));
        return a;
    }
    
    @MessageMapping("/dashboard/post/{deviceId}/{datastreamId}")
    @SendTo("/topic/data/{deviceId}/{datastreamId}")
    public Object receiveDashboardData(@DestinationVariable String deviceId, @DestinationVariable String datastreamId, String data) {
        return dashboardDataService.receiveDashboardData(deviceId, datastreamId, data);
    }
}
