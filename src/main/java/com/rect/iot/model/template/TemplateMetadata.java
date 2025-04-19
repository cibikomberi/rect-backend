package com.rect.iot.model.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.rect.iot.model.Datastream;

import java.util.List;
import java.util.Map;

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