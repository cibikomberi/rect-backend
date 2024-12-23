package com.rect.iot.config;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/hello") // Maps to "/app/hello"
    @SendTo("/topic/greetings") // Broadcasts to "/topic/greetings"
    public String handleMessage(String message) {
        return "Hello, " + message + "!";
    }
}
