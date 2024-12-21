package com.rect.iot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rect.iot.service.ThingService;


@RestController
public class ThingController {
    @Autowired
    private ThingService thingService;
    
    @PostMapping("/thing/log/{deviceId}")
    public ResponseEntity<?> logData(@PathVariable Long deviceId, @RequestBody Map<String, ?> dataMap) {
        List<String> invalidKeys = thingService.logData(deviceId, dataMap);
        if (invalidKeys == null) {
            return new ResponseEntity<>(HttpStatus.OK);            
        }
        return new ResponseEntity<>(invalidKeys, HttpStatus.BAD_REQUEST);
    }
    
}
