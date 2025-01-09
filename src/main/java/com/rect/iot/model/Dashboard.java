package com.rect.iot.model;

import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rect.iot.model.widget.DashboardData;

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
    private Set<String> associatedDevices;
    private String dashboardDataId;
    @Transient
    private DashboardData dashboardData;
    @Transient
    private String myAccess;
    
    @JsonIgnore
    private String owner;

    @JsonIgnore
    private Map<String, String> userAccess;
}