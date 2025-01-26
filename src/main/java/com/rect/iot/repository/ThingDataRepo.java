package com.rect.iot.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.ThingData;

@Repository
public interface ThingDataRepo extends MongoRepository<ThingData<?>, String> {
    List<ThingData<?>> findByDeviceIdAndDatastreamIdAndDateTimeAfter(String deviceId, String datastreamId, LocalDateTime time);
    
    List<ThingData<?>> deleteByDeviceIdAndDatastreamId(String deviceId, String datastreamId);

    ThingData<?> findFirstByDeviceIdAndDatastreamIdOrderByDateTimeDesc(String deviceId, String datastreamId);
}
