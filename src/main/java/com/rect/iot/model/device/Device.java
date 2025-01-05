package com.rect.iot.model.device;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // @ManyToOne
    // private User user;

    private Boolean inheritTemplate;
    private Boolean isUpToDate;
    private String version;
    private LocalDateTime lastActiveTime;

    // @JsonIgnore
    private String templateId;
    @Transient
    private String templateName;

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

    @Column(unique = true)
    private String apiKey;
}
