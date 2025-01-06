package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.DeviceConstants;

@Repository
public interface DeviceConstantsRepo extends MongoRepository<DeviceConstants, String> {
    DeviceConstants findByDeviceIdAndVersion(String deviceId, String version);
}
