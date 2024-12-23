package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.device.DeviceMetadata;

@Repository
public interface DeviceMetadataRepo extends MongoRepository<DeviceMetadata, String> {
    
}
