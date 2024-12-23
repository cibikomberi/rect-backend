package com.rect.iot.model.widget;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LineChartWidget extends Widget{
    @JsonProperty("xLabel")
    String xLabel;
    @JsonProperty("yLabel")
    String yLabel;
}
