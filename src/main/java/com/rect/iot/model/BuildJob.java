package com.rect.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

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
