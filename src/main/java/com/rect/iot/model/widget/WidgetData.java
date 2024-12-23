package com.rect.iot.model.widget;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.rect.iot.model.Datastream;

public class WidgetData {
    String label;
    List<Datastream> datastream;
    Map<String, ?> widgetConfig;

    @JsonAnyGetter
    public Map<String, ?> getWidgetConfig() {
        return widgetConfig;
    }
    @JsonAnySetter
    public void setWidgetConfig(Map<String, ?> widgetConfig) {
        this.widgetConfig = widgetConfig;
    }
}