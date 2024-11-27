package com.rect.iot.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {

    @JsonProperty("id")
    private String id;
    private NodePosition position;
    private String type;
    private NodeMeasured measured;
    private Map<String, String> data;
}
