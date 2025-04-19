package com.rect.iot.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MqttMessageSender {
    private final MessageChannel mqttOutboundChannel;

    public void sendMessage(String topic, Object payload, boolean retain) {
        String payloadString = convertPayloadToString(payload);
        Map<String, Object> headers = new HashMap<>();
        headers.put("mqtt_topic", topic);
        headers.put("mqtt_retained", retain);
        mqttOutboundChannel.send(new GenericMessage<>(payloadString, headers));
    }

    private String convertPayloadToString(Object payload) {
        if (payload instanceof String) {
            return (String) payload;
        } else if (payload instanceof byte[]) {
            return new String((byte[]) payload);
        } else {
            // Use a JSON library to serialize the object (e.g., Jackson)
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Failed to serialize payload to JSON", e);
            }
        }
    }
}
