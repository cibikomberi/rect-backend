package com.rect.iot.model.dashboard;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SliderWidget extends Widget {
    Integer min;
    Integer max;
    Integer step;
}