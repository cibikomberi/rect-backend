package com.rect.iot.model.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Node {

    @JsonProperty("id")
    private String id;
    private NodePosition position;
    private String type;
    private NodeMeasured measured;
    private Map<String, String> data;
}
