package com.rect.iot.config;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        String token = servletRequest.getServletRequest().getParameter("token");
        HttpHeaders headers = request.getHeaders();
        String authHeader = headers.getFirst("Authorization");
        System.out.println("hs");
        System.out.println(token);
        System.out.println(authHeader);
        System.out.println(request.getHeaders());
        return validateToken(token); // Implement your JWT or session validation logic
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // Optional: post-handshake logic
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        String token = servletRequest.getServletRequest().getParameter("token");
        HttpHeaders headers = request.getHeaders();
        String authHeader = headers.getFirst("Authorization");
        System.out.println("hs");
        System.out.println(token);
        System.out.println(authHeader);
        System.out.println(request.getHeaders());
        System.out.println(request);
    }

    private String extractToken(ServerHttpRequest request) {
        // Extract token logic (from headers, cookies, or query parameters)
        return "";
    }

    private boolean validateToken(String token) {
        // Validate the JWT or session
        return true;
    }
}
