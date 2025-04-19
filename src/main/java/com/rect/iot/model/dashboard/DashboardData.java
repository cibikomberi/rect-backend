package com.rect.iot.model.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document
public class DashboardData {
    @Id
    String id;
    String days;
    List<Layout> layout;
    Map<String, Widget> widgetData;
}


@Data
class Layout {
    String i;

    Short h;
    Short w;
    Short minH;
    Short minW;
    Short x;
    Short y;

    Boolean isDraggable;
    Boolean moved;
    @JsonProperty("static")
    Boolean isStatic;
}
