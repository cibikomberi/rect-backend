package com.rect.iot.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class DeviceMetadata {
    private String id;

    private List<Datastream> datastreams;
    private List<AccessControl> accessControls;
}