package com.rect.iot.model.device;

import com.rect.iot.model.Datastream;
import com.rect.iot.model.automation.Automation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class DeviceMetadata {
    private String id;

    private List<Datastream> datastreams;
    private List<Automation> automations;
    

    @Transient
    private Map<String, String> userAccess;
}