package com.rect.iot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.device.Device;

@Repository
public interface DeviceRepo extends MongoRepository<Device, String> {
    List<Device> findByOwner(String owner);
    List<Device> findByTemplateIdAndInheritTemplate(String templateId, Boolean inheritTemplate);
}
