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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rect.iot.model.Device;
import com.rect.iot.model.Flow;
import com.rect.iot.service.DeviceService;

@RestController
@CrossOrigin
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/{id}/list")
    public List<Device> getDevices(@PathVariable Long id) {
        return deviceService.getDevices(id);
    }

    @PostMapping("/{id}/new")
    public Device createDevice(@PathVariable Long id, @RequestBody ObjectNode json) {
        return deviceService.createDevice(id, json.get("name").asText(), json.get("board").asText());
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
