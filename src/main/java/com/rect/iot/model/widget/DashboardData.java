package com.rect.iot.model.widget;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document
public class DashboardData {
    @Id
    String id;
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
