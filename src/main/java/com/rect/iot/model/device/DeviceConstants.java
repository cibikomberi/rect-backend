package com.rect.iot.model.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
    @CompoundIndex(def = "{'deviceId': 1, 'version': 1}")
})
public class DeviceConstants {
    @Id
    private String id;

    private String deviceId;
    private String version;
    
    private String data;
}
