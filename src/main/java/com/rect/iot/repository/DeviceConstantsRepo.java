package com.rect.iot.repository;

import com.rect.iot.model.device.DeviceConstants;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceConstantsRepo extends MongoRepository<DeviceConstants, String> {
    DeviceConstants findByDeviceIdAndVersion(String deviceId, String version);
}
