package com.rect.iot.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {
    @Id
    private String id;
    private String name;
    private String access;
    private Boolean isDeviceSpecific; 
    private List<Long> associatedDevices;
    private String dashboardDataId;
}