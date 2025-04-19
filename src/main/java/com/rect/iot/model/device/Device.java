package com.rect.iot.model.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Device {
    @Id
    private String id;
    private String name;

    private String board;
    private String description;
    @Builder.Default
    private String status = "Offline";

    private Boolean inheritTemplate;
    private Boolean isDevDevice;
    // private Boolean inheritDashboardFromTemplate;
    @Builder.Default
    private String currentVersion = "";
    @JsonIgnore
    private String targetVersion;
    private LocalDateTime lastActiveTime;

    // @JsonIgnore
    private String templateId;
    @Transient
    private String templateName;
    @Transient
    private String myAccess;

    @JsonIgnore
    private String metadataId;

    private String dashboardId;
    
    @Transient
    private String flowId;

    @JsonIgnore
    private String owner;
    @JsonIgnore
    private Map<String, String> userAccess;

    private String image;

    @JsonIgnore
    private String apiKey;
}
