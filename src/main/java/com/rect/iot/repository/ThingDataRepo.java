package com.rect.iot.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.rect.iot.model.ThingData;

@Repository
public interface ThingDataRepo extends MongoRepository<ThingData<?>, String> {
    List<ThingData<?>> findByDeviceIdAndDatastreamId(Long deviceId, String datastreamId);

    ThingData<?> findFirstByDeviceIdAndDatastreamIdOrderByDateTimeDesc(Long deviceId, String datastreamId);
}
