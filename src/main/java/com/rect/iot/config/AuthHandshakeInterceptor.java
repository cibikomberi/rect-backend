package com.rect.iot.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.rect.iot.model.User;
import com.rect.iot.repository.UserRepo;
import com.rect.iot.service.DashboardService;
import com.rect.iot.service.JWTService;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DashboardService dashboardService;
    
    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        String token = extractToken(servletRequest);
        String username = jwtService.extractUserName(token);
        User user = userRepo.findByEmail(username);
        String dashboardId = servletRequest.getServletRequest().getParameter("dashboard");
        if (dashboardService.hasViewAccess(dashboardId, user.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @Nullable Exception exception) {
    }

    private String extractToken(ServletServerHttpRequest request) {
        return request.getServletRequest().getParameter("token");
    }
}
