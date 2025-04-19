package com.rect.iot.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rect.iot.model.events.MqttMessageEvent;
import com.rect.iot.service.ThingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MqttEventListener {
    private final  ThingService thingService;

    private final Pattern dataTopicPattern = Pattern.compile("rect/(.*?)/data");
    private final Pattern logTopicPattern = Pattern.compile("rect/(.*?)/log");
    private final Pattern statusTopicPattern = Pattern.compile("rect/(.*?)/status");
    private final Pattern syncTopicPattern = Pattern.compile("rect/(.*?)/sync");

    @EventListener
    public void handleTopic1(MqttMessageEvent event) throws JsonMappingException, JsonProcessingException {
        if (event.getTopic().matches("rect/[[0-9] | [a-z] | [A-Z]]+/data")) {
            saveThingData(event.getTopic(), event.getPayload());
        } else if (event.getTopic().matches("rect/[[0-9] | [a-z] | [A-Z]]+/log")) {
            saveThingLog(event.getTopic(), event.getPayload());
        } else if (event.getTopic().matches("rect/[[0-9] | [a-z] | [A-Z]]+/status")) {
            updateThingStatus(event.getTopic(), event.getPayload());
        } else if (event.getTopic().matches("rect/[[0-9] | [a-z] | [A-Z]]+/sync")) {
            syncDeviceWithServer(event.getTopic(), event.getPayload());
        }
    }


    private void syncDeviceWithServer(String topic, String payload) {
        Matcher matcher = syncTopicPattern.matcher(topic);
        if (matcher.find()) {
            List<String> datastreams = Arrays.asList(payload.split(","));
            thingService.syncDeviceWithServer(matcher.group(1), datastreams);
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
                List<String> invalidKeys = thingService.saveThingData(matcher.group(1), map, true);
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
}