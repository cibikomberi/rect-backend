package com.rect.iot.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rect.iot.service.ThingService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class ThingController {
    
    private final ThingService thingService;
    
    @PostMapping("/thing/log/{deviceId}")
    public ResponseEntity<?> saveThingData(@PathVariable String deviceId, @RequestBody Map<String, ?> dataMap) {
        List<String> invalidKeys = thingService.saveThingData(deviceId, dataMap, false);
        if (invalidKeys == null) {
            return new ResponseEntity<>(HttpStatus.OK);            
        }
        return new ResponseEntity<>(invalidKeys, HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/thing/update/{deviceId}")
    public ResponseEntity<?> updateThing(@PathVariable String deviceId, @RequestParam String version) throws IOException {
        return thingService.updateThing(deviceId, version);
    }
}
