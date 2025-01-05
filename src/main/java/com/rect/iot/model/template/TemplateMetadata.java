package com.rect.iot.model.template;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.rect.iot.model.Datastream;

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
public class TemplateMetadata {
    private String id;

    private List<Datastream> datastreams;

    @Transient
    private Map<String, String> userAccess;
}


