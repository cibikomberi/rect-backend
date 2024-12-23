package com.rect.iot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rect.iot.model.device.Device;
import com.rect.iot.model.device.DeviceMetadata;
import com.rect.iot.model.node.Flow;
import com.rect.iot.service.DeviceService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@CrossOrigin
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/devices/{id}")
    public List<Device> getDevices(@PathVariable Long id) {
        return deviceService.getDevices(id);
    }

    @PostMapping("/device")
    public Device createDevice(@RequestBody ObjectNode json) {
        return deviceService.createDevice(json.get("name").asText(), json.get("board").asText(), json.get("templateId").asLong());
    }

    @GetMapping("device/{id}")
    public Device getDeviceInfo(@PathVariable Long id) {
        return deviceService.getDeviceInfo(id);
    }
    
    @PutMapping("device/{id}")
    public Device updateDeviceInfo(@PathVariable Long id, @RequestPart Device info, @RequestPart DeviceMetadata metadata) {
        System.out.println(id);
        System.out.println(info);
        System.out.println(metadata);
        return deviceService.updateDeviceInfo(id, info, metadata);
    }

    @GetMapping("/device/metadata/{templateId}")
    public DeviceMetadata getDeviceMetadata(@PathVariable Long templateId){
        return deviceService.getDeviceMetadata(templateId);
    }

    @PostMapping("/flow/{id}/save")
    public ResponseEntity<?> saveFlow(@PathVariable Long id, @RequestBody Flow flow) {
        return new ResponseEntity<>(deviceService.saveDeviceFlow(id, flow), HttpStatus.OK);
    }


    // @PostMapping("/flow/{id}/build")
    // public ResponseEntity<?> buildAndDeployFlows(@PathVariable Long id, @RequestBody Flow flow) throws IOException, InterruptedException {
    //     return new ResponseEntity<>(deviceService.buildAndDeployFlows(id, flow), HttpStatus.OK);
    // }
}
