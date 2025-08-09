package com.rect.iot.repository;

import com.rect.iot.model.device.Device;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DeviceRepo extends MongoRepository<Device, String> {
    List<Device> findByOwner(String owner);
    List<Device> findByTemplateIdAndInheritTemplate(String templateId, Boolean inheritTemplate);
    List<Device> findByTemplateIdAndIsDevDevice(String templateId, Boolean isDevDevice);

    @Aggregation(pipeline = {
            "{ '$match': { 'owner': ?0 } }",
            "{ '$addFields': { 'templateIdObj': { '$toObjectId': '$templateId' } } }",
            "{ '$lookup': { 'from': 'template', 'localField': 'templateIdObj', 'foreignField': '_id', 'as': 'template' } }",            "{ '$unwind': { 'path': '$template', 'preserveNullAndEmptyArrays': true } }",
            "{ '$group': { '_id': null, " +
                    "'totalDevices': { '$sum': 1 }, " +
                    "'onlineDevices': { '$sum': { '$cond': [ { '$eq': ['$status', 'Online'] }, 1, 0 ] } }, " +
                    "'upToDateDevices': { '$sum': { '$cond': [ { '$eq': ['$currentVersion', '$template.productionVersion'] }, 1, 0 ] } } " +
                    "} }"
    })
    DeviceStats getDeviceStats(String owner);

    @Getter
    @Setter
    class DeviceStats {
        private int totalDevices;
        private int onlineDevices;
        private int upToDateDevices;
    }
}
