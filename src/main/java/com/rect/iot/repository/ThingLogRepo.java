package com.rect.iot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rect.iot.model.thing.ThingLog;

import java.time.LocalDateTime;
import java.util.List;


public interface ThingLogRepo extends MongoRepository<ThingLog, String> {
    List<ThingLog> findByDeviceId(String deviceId);
    List<ThingLog> findByDeviceIdAndTimeAfter(String deviceId, LocalDateTime time);
}
