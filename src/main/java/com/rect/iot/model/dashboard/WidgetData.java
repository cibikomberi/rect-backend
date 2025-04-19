package com.rect.iot.model.dashboard;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.rect.iot.model.Datastream;

import java.util.List;
import java.util.Map;

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