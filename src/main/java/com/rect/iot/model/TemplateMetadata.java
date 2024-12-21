package com.rect.iot.model;

import java.util.List;

import org.springframework.data.annotation.Transient;
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
public class TemplateMetadata {
    private String id;

    private List<Datastream> datastreams;
    private List<AccessControl> accessControls;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Datastream {
    private String identifier;
    private String name;
    private String type;
    private String unit;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AccessControl {
    private Long userId;

    @Transient
    private String name;
    @Transient
    private String email;

    private String access;
}