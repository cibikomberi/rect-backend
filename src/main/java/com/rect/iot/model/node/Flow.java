package com.rect.iot.model.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
