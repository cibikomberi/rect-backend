package com.rect.iot.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rect.iot.model.Datastream;
import com.rect.iot.model.User;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.node.Flow;
import com.rect.iot.service.DeviceService;



@RestController
@CrossOrigin
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/devices")
    public List<Device> getMyDevices() {
        return deviceService.getMyDevices();
    }

    @PostMapping("/device")
    public Device createDevice(@RequestBody ObjectNode json) {
        return deviceService.createDevice(json.get("name").asText(), json.get("board").asText(), json.get("templateId").asText());
    }

    @GetMapping("device/{id}")
    public Device getDeviceInfo(@PathVariable String id) throws IllegalAccessException{
        return deviceService.getDeviceInfo(id);
    }
    
    @PutMapping("device/{id}")
    public Device updateDeviceInfo(@PathVariable String id, @RequestPart Device info, @RequestPart(required = false) MultipartFile image) throws IllegalAccessException, IOException, InterruptedException {
        return deviceService.updateDeviceInfo(id, info, image);
    }

    @GetMapping("/device/metadata/{deviceId}")
    public DeviceMetadata getDeviceMetadata(@PathVariable String deviceId) throws IllegalAccessException{
        return deviceService.getDeviceMetadata(deviceId);
    }

    @PostMapping("/device/datastream/{deviceId}")
    public Datastream addDatastream(@PathVariable String deviceId, @RequestBody Datastream datastream) throws IllegalAccessException {
        return deviceService.addDatastream(deviceId, datastream);
    }
    
    @PutMapping("/device/datastream/{deviceId}/{datastreamId}")
    public Datastream updateDatastream(@PathVariable String deviceId, @PathVariable String datastreamId, @RequestBody Datastream datastream) throws IllegalAccessException {
        return deviceService.updateDatastream(deviceId, datastreamId, datastream);
    }

    @DeleteMapping("/device/datastream/{deviceId}/{datastreamId}")
    public String deleteDatastream(@PathVariable String deviceId, @PathVariable String datastreamId) throws IllegalAccessException {
        return deviceService.deleteDatastream(deviceId, datastreamId);
    }

    @PostMapping("/device/userAccess/{deviceId}")
    public String updateUserAccess(@PathVariable String deviceId, @RequestBody JsonNode json) throws IllegalAccessException{
        return deviceService.updateUserAccess(deviceId, json.get("user").asText(), json.get("access").asText());
    }

    @DeleteMapping("/device/userAccess/{deviceId}/{userId}")
    public String removeUserAccess(@PathVariable String deviceId, @PathVariable String userId) throws IllegalAccessException {
        return deviceService.removeUserAccess(deviceId, userId);
    }

    @GetMapping("/devices/shared")
    public List<Device> getSharedTemplates() {
        return deviceService.getSharedDevices();
    }

    @GetMapping("/device/image/{id}")
    public ResponseEntity<byte[]> resolveImage(@PathVariable String id){
        if (id.equals("null")) {
            return null;
        }
        return deviceService.resolveImage(id);
    }

    @PostMapping("/device/ota/{deviceId}")
    public String saveOta(@PathVariable String deviceId, @RequestPart("file") MultipartFile file, @RequestPart("version") JsonNode json) throws IllegalAccessException{ 
        return deviceService.saveOta(deviceId, file, json.get("version").asText())        ;
    }

    @PostMapping("/device/constants/{deviceId}/{version}")
    public String saveDeviceConstants(@PathVariable String deviceId,@PathVariable String version , @RequestBody String data) throws IllegalAccessException {
        return deviceService.saveDeviceConstants(deviceId, version, data);
    }
    
    @GetMapping("/friends")
    public List<User> getMyFriends(@RequestParam String param) {
        return deviceService.getFriends(param);
    }
    

    @PostMapping("/flow/{id}/save")
    public ResponseEntity<?> saveFlow(@PathVariable String id, @RequestBody Flow flow) {
        return new ResponseEntity<>(deviceService.saveDeviceFlow(id, flow), HttpStatus.OK);
    }


    // @PostMapping("/flow/{id}/build")
    // public ResponseEntity<?> buildAndDeployFlows(@PathVariable Long id, @RequestBody Flow flow) throws IOException, InterruptedException {
    //     return new ResponseEntity<>(deviceService.buildAndDeployFlows(id, flow), HttpStatus.OK);
    // }
}
