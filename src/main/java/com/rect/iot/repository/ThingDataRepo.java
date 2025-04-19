package com.rect.iot.repository;

import com.rect.iot.model.thing.ThingData;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ThingDataRepo extends MongoRepository<ThingData<?>, String> {
    List<ThingData<?>> findByDeviceIdAndDatastreamIdAndDateTimeAfter(String deviceId, String datastreamId, LocalDateTime time);
    
    List<ThingData<?>> deleteByDeviceIdAndDatastreamId(String deviceId, String datastreamId);

    ThingData<?> findFirstByDeviceIdAndDatastreamIdOrderByDateTimeDesc(String deviceId, String datastreamId);


    @Aggregation(pipeline = {
        "{ $match: { deviceId: ?0, datastreamId: { $in: ?1 } } }",
        "{ $sort: { dateTime: -1 } }",
        "{ $group: { _id: '$datastreamId', latestRecord: { $first: '$$ROOT' } } }",
        "{ $replaceRoot: { newRoot: '$latestRecord' } }"
    })
    List<ThingData<?>> findLatestValuesForDatastreams(String deviceId, List<String> datastreamIds);


}
