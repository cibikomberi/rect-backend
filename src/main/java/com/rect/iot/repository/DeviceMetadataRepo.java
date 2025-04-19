package com.rect.iot.repository;

import com.rect.iot.model.device.DeviceMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceMetadataRepo extends MongoRepository<DeviceMetadata, String> {
    
}
