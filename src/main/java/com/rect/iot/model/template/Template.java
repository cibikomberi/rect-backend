package com.rect.iot.model.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

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

    private String productionVersion;
    private String devVersion;
    private String buildVersion;

    @Transient
    private String myAccess;
    @JsonIgnore
    private String flowId;
    @JsonIgnore
    private String metadataId;
    @JsonIgnore
    @Indexed
    private String owner;
    @JsonIgnore
    private Map<String, String> userAccess;

    private String image;
}
