package com.rect.iot.model;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class BuildJob {
    private String id;
    private String version;
    private boolean deployed;
    private Map<String, String> devices;
    
}
