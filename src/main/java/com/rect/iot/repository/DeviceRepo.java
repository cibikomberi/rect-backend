package com.rect.iot.repository;

import com.rect.iot.model.device.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepo extends MongoRepository<Device, String> {
    List<Device> findByOwner(String owner);
    List<Device> findByTemplateIdAndInheritTemplate(String templateId, Boolean inheritTemplate);
    List<Device> findByTemplateIdAndIsDevDevice(String templateId, Boolean isDevDevice);
}
