// package com.rect.iot.config;

// import org.springframework.messaging.Message;
// import org.springframework.messaging.MessageChannel;
// import org.springframework.messaging.simp.stomp.StompCommand;
// import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
// import org.springframework.messaging.support.ChannelInterceptor;
// import org.springframework.stereotype.Component;

// @Component
// public class JwtChannelInterceptor implements ChannelInterceptor {

//     @Override
//     public Message<?> preSend(Message<?> message, MessageChannel channel) {
//         StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

//         if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//             // Extract JWT and validate (as shown earlier)
//             validateJwt(accessor);
//         } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//             // Validate subscription access
//             String topic = accessor.getDestination();
//             String username = accessor.getUser().getName();

//             if (!hasAccessToTopic(username, topic)) {
//                 throw new IllegalArgumentException("Access denied to topic: " + topic);
//             }
//         }

//         return message;
//     }