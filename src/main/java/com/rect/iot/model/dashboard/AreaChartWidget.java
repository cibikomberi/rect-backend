package com.rect.iot.model.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AreaChartWidget extends Widget{
    @JsonProperty("xLabel")
    String xLabel;
    @JsonProperty("yLabel")
    String yLabel;
}
