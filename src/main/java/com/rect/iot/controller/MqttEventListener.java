package com.rect.iot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rect.iot.model.events.MqttMessageEvent;
import com.rect.iot.service.ThingService;

@Component
public class MqttEventListener {
    @Autowired
    private ThingService thingService;
    @Autowired
    private MessageChannel mqttOutboundChannel;

    private final Pattern dataTopicPattern = Pattern.compile("rect/(.*?)/data");
    private final Pattern logTopicPattern = Pattern.compile("rect/(.*?)/log");
    private final Pattern statusTopicPattern = Pattern.compile("rect/(.*?)/status");

    @EventListener
    public void handleTopic1(MqttMessageEvent event) throws JsonMappingException, JsonProcessingException {
        if (event.getTopic().matches("rect/[[0-9] | [a-z] | [A-Z]]+/data")) {
            saveThingData(event.getTopic(), event.getPayload());
        } else if (event.getTopic().matches("rect/[[0-9] | [a-z] | [A-Z]]+/log")) {
            saveThingLog(event.getTopic(), event.getPayload());
        } else if (event.getTopic().matches("rect/[[0-9] | [a-z] | [A-Z]]+/status")) {
            updateThingStatus(event.getTopic(), event.getPayload());
        }
    }

    private void updateThingStatus(String topic, String payload) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        Matcher matcher = statusTopicPattern.matcher(topic);
        if (matcher.find()) {
            try {
                Map<String, String> map = mapper.readValue(payload, typeRef);
                thingService.updateThingStatus(matcher.group(1), map);
                if(map.get("status").equalsIgnoreCase("Online")) {
                    saveThingLog("[Device] " + map.get("status"), "LOG", matcher.group(1));
                } else {
                    saveThingLog("[Device] " + map.get("status"), "ERROR", matcher.group(1));
                }
            } catch (JsonProcessingException e) {
                saveThingLog("[Rect] Invalid JSON", "ERROR", matcher.group(1));
            } 
        }
    }

    private void saveThingData(String topic, String payload) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        Matcher matcher = dataTopicPattern.matcher(topic);
        if (matcher.find()) {
            try {
                Map<String, String> map = mapper.readValue(payload, typeRef);
                List<String> invalidKeys = thingService.saveThingData(matcher.group(1), map);
                if (invalidKeys != null) {
                    saveThingLog("[Rect] Invalid Datastreams: " + invalidKeys.toString(), "ERROR", matcher.group(1));
                }
            } catch (JsonProcessingException e) {
                saveThingLog("[Rect] Invalid JSON", "ERROR", matcher.group(1));
            }
        }
    }

    private void saveThingLog(String topic, String payload) {
        Matcher matcher = logTopicPattern.matcher(topic);
        if (matcher.find()) {
            saveThingLog("[Device] " + payload, "LOG", matcher.group(1));
        }
    }

    private void saveThingLog(String log, String type, String deviceId) {
        thingService.saveThingLog(log, type, deviceId);
    }

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