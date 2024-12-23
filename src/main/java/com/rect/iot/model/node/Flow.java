package com.rect.iot.model.node;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("Flows")
public class Flow {
    
    private String id;

    private List<Node> nodes;
    private List<Edge> edges;
}
