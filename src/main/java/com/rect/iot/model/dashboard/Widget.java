package com.rect.iot.model.dashboard;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rect.iot.model.Datastream;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,    // Use type property to distinguish subclasses
    include = JsonTypeInfo.As.PROPERTY, // Type information is included as a property
    property = "type"             // JSON field to indicate the type
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AreaChartWidget.class, name = "AreaChart"),
    @JsonSubTypes.Type(value = CircularGaugeWidget.class, name = "CircularGauge"),
    @JsonSubTypes.Type(value = GaugeWidget.class, name = "Gauge"),
    @JsonSubTypes.Type(value = LineChartWidget.class, name = "LineChart"),
    @JsonSubTypes.Type(value = NumberInputWidget.class, name = "NumberInput"),
    @JsonSubTypes.Type(value = SliderWidget.class, name = "Slider"),
    @JsonSubTypes.Type(value = ToggleWidget.class, name = "Toggle"),
    @JsonSubTypes.Type(value = LabelWidget.class, name = "Label"),
    @JsonSubTypes.Type(value = LogWidget.class, name = "Log")
})
public abstract class Widget {
    private String label;
    private List<Datastream> datastream;
}