package com.rect.iot.model;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Template {
    @Id
    private String id;
    private String name;

    private String board;
    private String description;

    @JsonIgnore
    private String flowId;
    @JsonIgnore
    private String metadataId;

    @JsonIgnore
    private String owner;

    @JsonIgnore
    private Map<String, String> userAccess;
}
