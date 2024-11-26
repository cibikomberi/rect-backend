package com.rect.iot.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private String id;
    private NodePosition position;
    private String type;
    private NodeMeasured measured;
    private ObjectNode data;
}
