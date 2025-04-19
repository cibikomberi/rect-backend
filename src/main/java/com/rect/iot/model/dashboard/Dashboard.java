package com.rect.iot.model.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

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
    @JsonIgnore
    private String mobileDashboardDataId;
    @JsonIgnore
    private String tabletDashboardDataId;
    @JsonIgnore
    private String largeDashboardDataId;
    
    @Transient
    private DashboardData dashboardData;
    @Transient
    private String myAccess;
    
    @JsonIgnore
    private String owner;

    @JsonIgnore
    private Map<String, String> userAccess;
}