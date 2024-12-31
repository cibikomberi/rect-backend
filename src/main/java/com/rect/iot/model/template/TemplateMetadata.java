package com.rect.iot.model.template;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.rect.iot.model.Datastream;

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
}


