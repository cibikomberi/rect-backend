package com.rect.iot.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionValidationInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination(); // Topic/Queue being subscribed to
            String token = accessor.getFirstNativeHeader("Authorization"); // Extract token

            // if (!validateSubscription(token, destination)) {
                //     throw new IllegalArgumentException("Unauthorized subscription to: " + destination);
                // }
                System.out.println("sub");
                System.out.println(destination);
                System.out.println(token);
            }

        return message;
    }

    private boolean validateSubscription(String token, String destination) {
        // Example logic: Validate token and check permissions for the destination
        if (token == null || !validateToken(token)) {
            return false;
        }

        // Check if the user is allowed to subscribe to this destination
        String userRole = extractUserRoleFromToken(token);
        return hasAccess(userRole, destination);
    }

    private boolean validateToken(String token) {
        // Token validation logic here
        return true; // Replace with actual validation
    }

    private String extractUserRoleFromToken(String token) {
        // Extract user role from token
        return "USER"; // Replace with actual logic
    }

    private boolean hasAccess(String role, String destination) {
        // Role-based access control for destinations
        if ("USER".equals(role) && destination.startsWith("/topic/private")) {
            return false; // Restrict USERS from accessing private topics
        }
        return true;
    }
}
