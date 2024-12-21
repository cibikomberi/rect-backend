package com.rect.iot.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;

    private String board;
    private String description;

    // @ManyToOne
    // private User user;

    private Boolean inheritTemplate;
    private Boolean isUpToDate;
    private LocalDateTime lastActiveTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Template template;
    @JsonIgnore
    private String metadataId;
    private String dashboardId;
    
    @Transient
    private String templateName;

    @Transient
    private String flowId;

    @Column(unique = true)
    private String apiKey;
}
