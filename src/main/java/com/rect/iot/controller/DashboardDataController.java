package com.rect.iot.controller;


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

    @MessageMapping("/dashboard/get/{dashboardId}/{widgetId}")
    @SendTo("/topic/data/{dashboardId}/{widgetId}")
    public Object resolveDashboardData(@DestinationVariable String dashboardId, @DestinationVariable String widgetId ) {
        System.out.println(dashboardId+widgetId);
        return dashboardDataService.resolveDashboardData(dashboardId, widgetId);
    }
    
    @MessageMapping("/dashboard/post/{dashboardId}/{widgetId}")
    @SendTo("/topic/data/{dashboardId}/{widgetId}")
    public Object receiveDashboardData(@DestinationVariable String dashboardId, @DestinationVariable String widgetId, String data) {
        return dashboardDataService.receiveDashboardData(dashboardId, widgetId, data);
        // System.out.println(data);
        // return null;
    }

    @GetMapping("/abcd/{dashboardId}/{widgetId}")
    @ResponseBody
    public Object resolveDashboardDat(@PathVariable String dashboardId, @PathVariable String widgetId) {
        return dashboardDataService.resolveDashboardData(dashboardId, widgetId);
    }
}
