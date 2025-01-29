package com.rect.iot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.rect.iot.model.Dashboard;
import com.rect.iot.repository.DashboardRepo;
import com.rect.iot.service.DashboardService;
import com.rect.iot.service.JWTService;

@Component
public class SubscriptionValidationInterceptor implements ChannelInterceptor {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private DashboardRepo dashboardRepo;
    @Autowired
    private DashboardService dashboardService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination == null) {
                return null;
            }
            String deviceId = destination.split("/")[3];
            String token = accessor.getFirstNativeHeader("Authorization");
            String dashboardId = accessor.getFirstNativeHeader("Dashboard");
            if (dashboardId == null) {
                return null;
            }
            Dashboard dashboard = dashboardRepo.findById(dashboardId).get();
            if (dashboard.getAccess().equals("Public")) {
                return message;
            }
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String userId = jwtService.extractId(token);
                if (dashboardService.hasViewAccess(dashboard, userId)) {
                    if (dashboard.getAssociatedDevices().contains(deviceId)) {
                        return message;
                    }
                }
            }
            return null;
        }
        return message;
    }
}
