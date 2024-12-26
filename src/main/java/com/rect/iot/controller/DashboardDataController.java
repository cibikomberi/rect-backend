package com.rect.iot.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rect.iot.service.DashboardDataService;

@Controller
public class DashboardDataController {
    @Autowired
    private DashboardDataService dashboardDataService;

    @MessageMapping("/dashboard/get/{deviceId}/{datastreamId}")
    @SendTo("/topic/data/{deviceId}/{datastreamId}")
    public Object resolveDashboardData(@DestinationVariable Long deviceId, @DestinationVariable String datastreamId, String range ) {
        // System.out.println(dashboardId+widgetId);
        Map<String, Object> a = new HashMap<>();
        a.put("type", range);
        a.put("data", dashboardDataService.resolveDashboardData(deviceId, datastreamId, range));
        return a;
    }
    
    @MessageMapping("/dashboard/post/{deviceId}/{datastreamId}")
    @SendTo("/topic/data/{deviceId}/{datastreamId}")
    public Object receiveDashboardData(@DestinationVariable Long deviceId, @DestinationVariable String datastreamId, String data) {
        return dashboardDataService.receiveDashboardData(deviceId, datastreamId, data);
        // System.out.println(data);
        // return null;
    }

    @GetMapping("/abcd/{deviceId}/{datastreamId}/{range}")
    @ResponseBody
    public Object resolveDashboardDat(@PathVariable Long deviceId,@PathVariable String datastreamId,@PathVariable String range) {
        return dashboardDataService.resolveDashboardData(deviceId, datastreamId, range);
    }
}
