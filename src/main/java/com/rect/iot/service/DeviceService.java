package com.rect.iot.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;

import com.rect.iot.model.Device;
import com.rect.iot.model.DeviceMetadata;
import com.rect.iot.model.Flow;
import com.rect.iot.model.Template;
import com.rect.iot.repository.DeviceMetadataRepo;
import com.rect.iot.repository.DeviceRepo;
import com.rect.iot.repository.FlowRepo;

@Service
public class DeviceService {
    
    @Autowired
    private DeviceRepo deviceRepo;
    @Autowired
    private DeviceMetadataRepo deviceMetadataRepo;
    @Autowired
    private FlowRepo flowRepo;
    
    public Device createDevice( String name, String board, Long templateId) {
        DeviceMetadata deviceMetadata = deviceMetadataRepo.save(DeviceMetadata.builder()
                .accessControls(new ArrayList<>())
                .datastreams(new ArrayList<>())
                .build());
        return deviceRepo.save(Device.builder()
                .board(board)
                .name(name)
                .lastActiveTime(LocalDateTime.now())
                .metadataId(deviceMetadata.getId())
                .template(Template.builder().id(templateId).build())
                .build());
    }

    public List<Device> getDevices(Long id) {
        return deviceRepo.findAll();
    }
    
    public Device getDeviceInfo(Long id) {
        Device device = deviceRepo.findById(id).get();
        device.setTemplateName(device.getTemplate().getName());
        
        return device;
    }

    public Device updateDeviceInfo(@PathVariable Long id, @RequestPart Device newInfo, @RequestPart DeviceMetadata newMetadata) {
        Device device = deviceRepo.findById(id).get();
        DeviceMetadata metadata = deviceMetadataRepo.findById(device.getMetadataId()).get();

        device.setName(newInfo.getName());
        device.setDescription(newInfo.getDescription());
        device.setInheritTemplate(newInfo.getInheritTemplate());

        metadata.setDatastreams(newMetadata.getDatastreams());
        metadata.setAccessControls(newMetadata.getAccessControls());

        deviceMetadataRepo.save(metadata);

        return deviceRepo.save(device);
    }

    public DeviceMetadata getDeviceMetadata(Long deviceId) {
        return deviceMetadataRepo.findById(deviceRepo
                .findById(deviceId)
                .get()
                .getMetadataId())
            .get();
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
