package com.rect.iot.model.device;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class DeviceConstants {
    @Id
    private String id;

    private String deviceId;
    private String version;
    
    private String data;
}
