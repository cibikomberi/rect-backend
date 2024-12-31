package com.rect.iot.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rect.iot.model.widget.DashboardData;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
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
    private List<String> associatedDevices;
    private String dashboardDataId;
    @Transient
    private DashboardData dashboardData;

    @JsonIgnore
    private String owner;

    @JsonIgnore
    private Map<String, String> userAccess;
}