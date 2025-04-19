package com.rect.iot.model.dashboard;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GaugeWidget extends Widget {
    Integer min;
    Integer max;
}
