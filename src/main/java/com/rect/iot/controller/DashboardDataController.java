package com.rect.iot.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.rect.iot.service.DashboardDataService;

@Controller
public class DashboardDataController {
    @Autowired
    private DashboardDataService dashboardDataService;
@Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/dashboard/get/{deviceId}/{datastreamId}")
    @SendTo("/topic/data/{deviceId}/{datastreamId}")
    public Object resolveDashboardData(@DestinationVariable String deviceId, @DestinationVariable String datastreamId, String range, StompHeaderAccessor headerAccessor) {
        Map<String, Object> a = new HashMap<>();
        a.put("type", range);
        a.put("data", dashboardDataService.resolveDashboardData(deviceId, datastreamId, range));

       
        // String username = headerAccessor.getUser().getName();
        

        // messagingTemplate.convertAndSendToUser(username, "/topic/data/" + deviceId + "/" + datastreamId, a);
        return a;
    }
    
    @MessageMapping("/dashboard/post/{deviceId}/{datastreamId}")
    @SendTo("/topic/data/{deviceId}/{datastreamId}")
    public Object receiveDashboardData(@DestinationVariable String deviceId, @DestinationVariable String datastreamId, String data) {
        return dashboardDataService.receiveDashboardData(deviceId, datastreamId, data);
    }
}
