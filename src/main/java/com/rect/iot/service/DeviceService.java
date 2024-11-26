package com.rect.iot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rect.iot.model.Device;
import com.rect.iot.model.Flow;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.FlowRepo;

@Service
public class DeviceService {
    
    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private FlowRepo flowRepo;
    
    public Device createDevice(Long id, String name, String board) {
        return deviceRepo.save(Device.builder()
                .board(board)
                .name(name)
                .build());
    }

    public List<Device> getDevices(Long id) {
        return deviceRepo.findAll();
    }

    public Flow saveDeviceFlow(Long deviceId, Flow flow) {
        //Get active device
        Device device = deviceRepo.findById(deviceId).get();
        //Set the existing id
        flow.setId(device.getFlowId());
        Flow savedFlow = flowRepo.save(flow);

        // Save the flow
        device.setFlowId(savedFlow.getId());
        deviceRepo.save(device);

        return flow;
    }
}
