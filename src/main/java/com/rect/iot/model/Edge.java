package com.rect.iot.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Edge {
    private String id;
    private String source;
    private String target;
    private String sourceHandle;
    private String targetHandle;
    private Boolean animated;
}
